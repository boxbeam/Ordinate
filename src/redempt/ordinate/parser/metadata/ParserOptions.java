package redempt.ordinate.parser.metadata;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.component.DescriptionComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.parser.TagProcessor;
import redempt.ordinate.parser.argument.ArgumentParser;
import redempt.ordinate.parser.argument.DefaultArgumentParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParserOptions<T> {

	public static <T> ParserOptions<T> getDefaults(ComponentFactory<T> componentFactory) {
		ParserOptions<T> options = new ParserOptions<>(new DefaultArgumentParser<>());
		options.contextProviders.put("self", ContextProvider.create("self", null, CommandContext::sender));
		options.argumentTypes.put("string", new ArgType<>("string", (ctx, str) -> str, (ctx, str) -> Collections.emptyList()));
		options.argumentTypes.put("int", numberArgType("int", Integer::parseInt, componentFactory));
		options.argumentTypes.put("float", numberArgType("float", Float::parseFloat, componentFactory));
		options.argumentTypes.put("long", numberArgType("long", Long::parseLong, componentFactory));
		options.argumentTypes.put("double", numberArgType("double", Double::parseDouble, componentFactory));
		options.argumentTypes.put("boolean", new ArgType<>("boolean", (ctx, str) -> Boolean.parseBoolean(str), (ctx, str) -> Arrays.asList("true", "false")));
		options.tagProcessors.put("help", TagProcessor.create("help", (cmd, str) -> {
			cmd.getPipeline().addComponent(new DescriptionComponent<>(str));
			return cmd;
		}));
		options.tagProcessors.put("context", TagProcessor.create("context", (cmd, str) -> {
			String[] split = str.split(" ");
			for (String name : split) {
				ContextProvider<T, ?> provider = options.getContextProvider(name);
				cmd.getPipeline().addComponent(componentFactory.createContext(provider, name));
			}
			return cmd;
		}));
		return options;
	}

	private static <T, V extends Number & Comparable<V>> ArgType<T, V> numberArgType(String name, Function<String, V> numberParser, ComponentFactory<T> componentFactory) {
		return new ArgType<T, V>(name, (ctx, str) -> numberParser.apply(str), (ctx, str) -> Collections.emptyList())
				.constraint(componentFactory.createNumberConstraintParser(numberParser));
	}

	private ArgumentParser<T> argumentParser;
	private Map<String, TagProcessor<T>> tagProcessors = new HashMap<>();
	private Map<String, ArgType<T, ?>> argumentTypes = new HashMap<>();
	private Map<String, ContextProvider<T, ?>> contextProviders = new HashMap<>();

	public ParserOptions(ArgumentParser<T> argumentParser) {
		this.argumentParser = argumentParser;
	}

	public ArgumentParser<T> getArgumentParser() {
		return argumentParser;
	}

	public void setArgumentParser(ArgumentParser<T> argumentParser) {
		this.argumentParser = argumentParser;
	}

	private <V> V getOrError(Map<String, V> map, String key, Supplier<String> error) {
		V value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException(error.get());
		}
		return value;
	}

	public TagProcessor<T> getTagProcessor(String name) {
		return getOrError(tagProcessors, name, () -> "Missing TagProcessor for tag " + name);
	}

	public ArgType<T, ?> getArgType(String name) {
		return getOrError(argumentTypes, name, () -> "Missing ArgType with name " + name);
	}

	public ContextProvider<T, ?> getContextProvider(String name) {
		return getOrError(contextProviders, name, () -> "Missing ContextProvider with name " + name);
	}

	public Map<String, TagProcessor<T>> getTagProcessors() {
		return tagProcessors;
	}

	public Map<String, ArgType<T, ?>> getArgumentTypes() {
		return argumentTypes;
	}

	public Map<String, ContextProvider<T, ?>> getContextProviders() {
		return contextProviders;
	}

}
