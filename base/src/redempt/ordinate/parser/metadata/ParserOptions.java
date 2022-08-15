package redempt.ordinate.parser.metadata;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.command.postarg.PostArgumentSubcommand;
import redempt.ordinate.component.DescriptionComponent;
import redempt.ordinate.component.HelpSubcommandComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.parser.TagProcessor;
import redempt.ordinate.parser.argument.ArgumentParser;
import redempt.ordinate.parser.argument.DefaultArgumentParser;
import redempt.ordinate.processing.MiscUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ParserOptions<T> {

	public static <T> ParserOptions<T> getDefaults(ComponentFactory<T> componentFactory) {
		ParserOptions<T> options = new ParserOptions<>(new DefaultArgumentParser<>());
		options.insert(ContextProvider.create("self", null, CommandContext::sender));
		options.insert(new ArgType<>("string", (ctx, str) -> str));
		options.insert(numberArgType("int", Integer::parseInt, componentFactory));
		options.insert(numberArgType("float", Float::parseFloat, componentFactory));
		options.insert(numberArgType("long", Long::parseLong, componentFactory));
		options.insert(numberArgType("double", Double::parseDouble, componentFactory));
		options.insert(new ArgType<>("boolean", (ctx, str) -> MiscUtils.parseBoolean(str)).completer((ctx, str) -> Arrays.asList("true", "false")));
		options.tagProcessors.put("help", TagProcessor.create("help", (cmd, str) -> {
			Optional<DescriptionComponent<?>> optionalDescription =
					cmd.getPipeline().getComponents().stream()
					.filter(c -> c instanceof DescriptionComponent).findFirst().map(c -> (DescriptionComponent<?>) c);
			optionalDescription.ifPresent(c -> c.addLine(str));
			if (!optionalDescription.isPresent()) {
				cmd.getPipeline().addComponent(new DescriptionComponent<>(str));
			}
		}));
		options.tagProcessors.put("context", TagProcessor.create("context", (cmd, str) -> {
			String[] split = str.split(" ");
			for (String name : split) {
				ContextProvider<T, ?> provider = options.getContextProvider(name);
				cmd.getPipeline().addComponent(componentFactory.createContext(provider, name));
			}
		}));
		options.tagProcessors.put("noHelpSubcommand", TagProcessor.create("noHelpSubcommand", (cmd, str) -> {
			cmd.getPipeline().getComponents().removeIf(c -> c instanceof HelpSubcommandComponent);
		}));
		options.tagProcessors.put("postArgument", TagProcessor.create("postArgument", (cmd, str) -> {
			PostArgumentSubcommand.makePostArgument(cmd);
		}));
		return options;
	}

	private static <T, V extends Number & Comparable<V>> ArgType<T, V> numberArgType(String name, Function<String, V> numberParser, ComponentFactory<T> componentFactory) {
		return new ArgType<T, V>(name, (ctx, str) -> numberParser.apply(str))
				.constraint(componentFactory.createNumberConstraintParser(numberParser));
	}

	private ArgumentParser<T> argumentParser;
	private Map<String, TagProcessor<T>> tagProcessors = new HashMap<>();
	private Map<String, ArgType<T, ?>> argumentTypes = new HashMap<>();
	private Map<String, ContextProvider<T, ?>> contextProviders = new HashMap<>();
	private String helpSubcommandName = "help";

	public ParserOptions(ArgumentParser<T> argumentParser) {
		this.argumentParser = argumentParser;
	}

	private void insert(ArgType<?, ?> argType) {
		argumentTypes.put(argType.getName(), (ArgType<T, ?>) argType);
	}
	
	private void insert(ContextProvider<?, ?> provider) {
		contextProviders.put(provider.getName(), (ContextProvider<T, ?>) provider);
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

	public String getHelpSubcommandName() {
		return helpSubcommandName;
	}
	
	public void setHelpSubcommandName(String helpSubcommandName) {
		this.helpSubcommandName = helpSubcommandName;
	}
	
	public Map<String, ContextProvider<T, ?>> getContextProviders() {
		return contextProviders;
	}

}
