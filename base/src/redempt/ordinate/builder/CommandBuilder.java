package redempt.ordinate.builder;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.DescriptionComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.processing.CommandPipeline;

import java.util.function.Consumer;
import java.util.function.Function;

public class CommandBuilder<T, B extends CommandBuilder<T, B>> {

	protected CommandPipeline<T> pipeline = new CommandPipeline<>();
	protected CommandManager<T> manager;
	protected ComponentFactory<T> componentFactory;
	private BuilderOptions<T> options;
	private CommandBuilderFactory<T, B> builderFactory;
	private String[] names;
	
	public CommandBuilder(String[] names, CommandManager<T> manager, BuilderOptions<T> options, CommandBuilderFactory<T, B> builderFactory) {
		this.manager = manager;
		componentFactory = manager.getComponentFactory();
		this.names = names;
		this.options = options;
		this.builderFactory = builderFactory;
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
		consumer.accept(builder);
		pipeline.addComponent(builder.build());
		return (B) this;
	}
	
	public B subcommand(String name, Consumer<B> consumer) {
		return subcommand(new String[] {name}, consumer);
	}
	
	public Command<T> build() {
		return new Command<>(names, pipeline);
	}
	
}
