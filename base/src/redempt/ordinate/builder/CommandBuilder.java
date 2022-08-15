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
	
	public B help(String help) {
		pipeline.addComponent(new DescriptionComponent<>(help));
		return (B) this;
	}
	
	public B arg(Class<?> type, String name) {
		if (type.isArray()) {
			pipeline.addComponent(componentFactory.createVariableLengthArgument(options.getType(type.getComponentType()), false, name));
		} else {
			pipeline.addComponent(componentFactory.createArgument(options.getType(type), name));
		}
		return (B) this;
	}
	
	public <V> B consumingArg(Class<V> type, String name, boolean optional, Function<CommandContext<T>, V> defaultValue) {
		ContextProvider<T, V> context = defaultValue == null ? null : ContextProvider.create(null, "Failed to get default value for " + name, defaultValue);
		pipeline.addComponent(componentFactory.createConsumingArgument((ArgType<T, V>) options.getType(type), optional, context, name));
		return (B) this;
	}
	
	public <V> B consumingArg(Class<V> type, String name) {
		return consumingArg(type, name, false, ctx -> null);
	}
	
	public <V> B optionalArg(Class<V> type, String name, Function<CommandContext<T>, V> defaultValue) {
		ContextProvider<T, V> context = defaultValue == null ? null : ContextProvider.create(null, "Failed to get default value for " + name, defaultValue);
		if (type.isArray()) {
			pipeline.addComponent(componentFactory.createVariableLengthArgument(options.getType(type.getComponentType()), true, name));
		} else {
			pipeline.addComponent(componentFactory.createOptionalArgument((ArgType<T, V>) options.getType(type), context, name));
		}
		return (B) this;
	}
	
	public B subcommand(String[] names, Consumer<B> consumer) {
		B builder = builderFactory.create(names, manager, options);
		((CommandBuilder<T, B>) builder).deferred = deferred;
		consumer.accept(builder);
		pipeline.addComponent(builder.build());
		return (B) this;
	}
	
	public B noHelpSubcommand() {
		pipeline.getComponents().removeIf(c -> c instanceof HelpSubcommandComponent);
		return (B) this;
	}
	
	public B subcommand(String name, Consumer<B> consumer) {
		return subcommand(new String[] {name}, consumer);
	}
	
	public B handler(Consumer<CommandArguments<T>> handler) {
		pipeline.addComponent(componentFactory.createDispatch(new BuilderDispatcher<>(handler)));
		return (B) this;
	}
	
	public B boolFlag(String... names) {
		pipeline.addComponent(componentFactory.createBooleanFlag(names));
		return (B) this;
	}
	
	public B postArgument() {
		postArg = true;
		return (B) this;
	}
	
	public Command<T> build() {
		Command<T> cmd = new Command<>(names, pipeline);
		cmd.getPipeline().getComponents().forEach(c -> c.setParent(cmd));
		if (postArg) {
			deferred.add(() -> PostArgumentSubcommand.makePostArgument(cmd));
		}
		return cmd;
	}
	
	public void register() {
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
