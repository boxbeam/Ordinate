package redempt.ordinate.builder;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.command.Command;
import redempt.ordinate.command.CommandBase;
import redempt.ordinate.command.postarg.PostArgumentSubcommand;
import redempt.ordinate.component.DescriptionComponent;
import redempt.ordinate.component.HelpSubcommandComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.processing.CommandPipeline;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Builds a command, provided by {@link CommandManager}
 * @param <T> The sender type
 * @param <B> The type of the builder itself
 * @author Redempt
 */
public class CommandBuilder<T, B extends CommandBuilder<T, B>> {

	protected CommandPipeline<T> pipeline = new CommandPipeline<>();
	protected CommandManager<T> manager;
	protected ComponentFactory<T> componentFactory;
	protected List<Runnable> deferred = new ArrayList<>();
	private BuilderOptions<T> options;
	private CommandBuilderFactory<T, B> builderFactory;
	private String[] names;
	private boolean postArg;
	
	public CommandBuilder(String[] names, CommandManager<T> manager, BuilderOptions<T> options, CommandBuilderFactory<T, B> builderFactory) {
		this.manager = manager;
		componentFactory = manager.getComponentFactory();
		this.names = names;
		this.options = options;
		this.builderFactory = builderFactory;
		if (options.getHelpSubcommandName() != null) {
			pipeline.addComponent(new HelpSubcommandComponent<>(options.getHelpSubcommandName()));
		}
	}
	
	/**
	 * Sets the help message of the command
	 * @param help The help message
	 * @return Itself
	 */
	public B help(String help) {
		pipeline.addComponent(new DescriptionComponent<>(help));
		return (B) this;
	}
	
	/**
	 * Adds a new argument to the command
	 * @param type The type of the argument
	 * @param name The name of the argument
	 * @return Itself
	 */
	public B arg(Class<?> type, String name) {
		if (type.isArray()) {
			pipeline.addComponent(componentFactory.createVariableLengthArgument(options.getType(type.getComponentType()), false, name));
		} else {
			pipeline.addComponent(componentFactory.createArgument(options.getType(type), name));
		}
		return (B) this;
	}
	
	/**
	 * Adds a new consuming argument to the command. Consuming arguments take all the remaining arguments in the command and parse them as a single string.
	 * @param type The type of the argument
	 * @param name The name of the argument
	 * @param optional Whether the argument should be optional
	 * @param defaultValue The default value if no value is provided
	 * @return Itself
	 * @param <V> The argument type
	 */
	public <V> B consumingArg(Class<V> type, String name, boolean optional, Function<CommandContext<T>, V> defaultValue) {
		ContextProvider<T, V> context = defaultValue == null ? null : ContextProvider.create(null, "Failed to get default value for " + name, defaultValue);
		pipeline.addComponent(componentFactory.createConsumingArgument((ArgType<T, V>) options.getType(type), optional, context, name));
		return (B) this;
	}
	
	/**
	 * Adds a new consuming argument to the command. Consuming arguments take all the remaining arguments in the command and parse them as a single string.
	 * @param type The type of the argument
	 * @param name The name of the argument
	 * @return Itself
	 * @param <V> The argument type
	 */
	public <V> B consumingArg(Class<V> type, String name) {
		return consumingArg(type, name, false, ctx -> null);
	}
	
	/**
	 * Adds a new optional argument to the command
	 * @param type The type of the argument
	 * @param name The name of the argument
	 * @param defaultValue The default value of the argument, pass null for no default
	 * @return Itself
	 * @param <V> The argument type
	 */
	public <V> B optionalArg(Class<V> type, String name, Function<CommandContext<T>, V> defaultValue) {
		ContextProvider<T, V> context = defaultValue == null ? null : ContextProvider.create(null, "Failed to get default value for " + name, defaultValue);
		if (type.isArray()) {
			pipeline.addComponent(componentFactory.createVariableLengthArgument(options.getType(type.getComponentType()), true, name));
		} else {
			pipeline.addComponent(componentFactory.createOptionalArgument((ArgType<T, V>) options.getType(type), context, name));
		}
		return (B) this;
	}
	
	/**
	 * Creates a subcommand for the command
	 * @param names The names of the subcommand
	 * @param consumer A function which will mutate the created builder, after which its command will be added as a child of this one
	 * @return Itself
	 */
	public B subcommand(String[] names, Consumer<B> consumer) {
		B builder = builderFactory.create(names, manager, options);
		((CommandBuilder<T, B>) builder).deferred = deferred;
		consumer.accept(builder);
		pipeline.addComponent(builder.build());
		return (B) this;
	}
	
	/**
	 * Creates a subcommand for the command
	 * @param name The names of the subcommand
	 * @param consumer A function which will mutate the created builder, after which its command will be added as a child of this one
	 * @return Itself
	 */
	public B subcommand(String name, Consumer<B> consumer) {
		return subcommand(new String[] {name}, consumer);
	}
	
	
	/**
	 * Removes the auto-generated help subcommand
	 * @return Itself
	 */
	public B noHelpSubcommand() {
		pipeline.getComponents().removeIf(c -> c instanceof HelpSubcommandComponent);
		return (B) this;
	}
	
	/**
	 * Assigns a handler that will be run when the command is executed
	 * @param handler The handler
	 * @return Itself
	 */
	public B handler(Consumer<CommandArguments<T>> handler) {
		pipeline.addComponent(componentFactory.createDispatch(new BuilderDispatcher<>(handler)));
		return (B) this;
	}
	
	/**
	 * Adds a new boolean flag to the command, which will be passed as true if present in the command when run and false otherwise
	 * @param names The names of the flag, must all start with a dash
	 * @return Itself
	 */
	public B boolFlag(String... names) {
		pipeline.addComponent(componentFactory.createBooleanFlag(names));
		return (B) this;
	}
	
	/**
	 * Marks the command as post-argument, meaning it will be positioned after the arguments of its parent and will be able to access those arguments
	 * @return Itself
	 */
	public B postArgument() {
		postArg = true;
		return (B) this;
	}
	
	protected Command<T> build() {
		Command<T> cmd = new Command<>(names, pipeline);
		cmd.getPipeline().getComponents().forEach(c -> c.setParent(cmd));
		if (postArg) {
			deferred.add(() -> PostArgumentSubcommand.makePostArgument(cmd));
		}
		return cmd;
	}
	
	/**
	 * Registers the command. Do not call on subcommands.
	 * @return The built command
	 */
	public CommandBase<T> register() {
		Command<T> command = build();
		Queue<Command<T>> queue = new ArrayDeque<>();
		queue.add(command);
		deferred.forEach(Runnable::run);
		while (!queue.isEmpty()) {
			Command<T> next = queue.poll();
			next.preparePipeline(manager);
			queue.addAll(next.getSubcommands());
		}
		CommandBase<T> base = new CommandBase<>(Collections.singletonList(command), manager);
		manager.getRegistrar().register(base);
		return base;
	}
	
	private static class BuilderDispatcher<T> implements CommandDispatcher<T> {
		
		private Map<String, Integer> indexMap = null;
		private Consumer<CommandArguments<T>> consumer;
		
		public BuilderDispatcher(Consumer<CommandArguments<T>> consumer) {
			this.consumer = consumer;
		}
		
		private void initMap(Command<T> cmd) {
			if (indexMap != null) {
				return;
			}
			indexMap = CommandArgumentMap.getMap(cmd);
		}
		
		@Override
		public void dispatch(CommandContext<T> context) {
			initMap(context.getCommand());
			CommandArguments<T> args = new CommandArguments<>(context.sender(), context.getAllParsed(), indexMap);
			consumer.accept(args);
		}
		
	}
	
}
