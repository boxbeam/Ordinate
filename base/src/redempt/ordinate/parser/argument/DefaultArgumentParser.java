package redempt.ordinate.parser.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.component.flag.BooleanFlagComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.parser.metadata.ParserOptions;
import redempt.ordinate.processing.CommandPipeline;
import redempt.redlex.data.Token;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DefaultArgumentParser<T> implements ArgumentParser<T> {

	@Override
	public <V> void parseArgument(Token argument, ParserOptions<T> options, ComponentFactory<T> componentFactory, CommandPipeline<T> pipeline) {
		if (argument.getType().getName().equals("flag")) {
			parseFlag(argument, options, componentFactory, pipeline);
			return;
		}
		ArgumentBuilder<T, V> builder = new ArgumentBuilder<>();
		Map<String, List<Token>> tokens = argument.allByNames("name", "type", "optional", "vararg", "consuming", "constraint", "defaultValue");
		String name = getDirect(tokens, "name").getValue();
		builder.setName(name);
		ArgType<T, ?> type = options.getArgType(getDirect(tokens, "type").getValue());
		builder.setType(type);
		getOptional(tokens, "optional").ifPresent(t -> builder.setOptional(true));
		getOptional(tokens, "vararg").ifPresent(t -> builder.setVararg(true));
		getOptional(tokens, "consuming").ifPresent(t -> builder.setConsuming(true));
		getOptional(tokens, "constraint").map(this::trimToken).ifPresent(s -> builder.setConstraint(type.getConstraintParser().parse(s)));
		getOptional(tokens, "defaultValue").map(this::trimToken).ifPresent(s -> builder.setDefaultValue(getDefaultValue(name, type, s, options)));
		builder.build(componentFactory).forEach(pipeline::addComponent);
	}
	
	private <V> void parseFlag(Token flag, ParserOptions<T> options, ComponentFactory<T> componentFactory, CommandPipeline<T> pipeline) {
		if (flag.getBaseString().charAt(flag.getStart()) == '-') {
			pipeline.addComponent(parseBooleanFlag(flag.getValue(), componentFactory));
			return;
		}
		Map<String, List<Token>> tokens = flag.allByNames("type", "name", "constraint", "defaultValue");
		String[] names = tokens.get("name").stream().map(Token::getValue).toArray(String[]::new);
		names[0] = "-" + names[0];
		FlagBuilder<T, V> builder = new FlagBuilder<>(names);
		ArgType<T, ?> type = options.getArgType(getDirect(tokens, "type").getValue());
		builder.setType(type);
		getOptional(tokens, "constraint").map(this::trimToken).ifPresent(s -> builder.setConstraint(type.getConstraintParser().parse(s)));
		getOptional(tokens, "defaultValue").map(this::trimToken).ifPresent(s -> builder.setDefaultValue(getDefaultValue(names[0], type, s, options)));
		builder.build(componentFactory).forEach(pipeline::addComponent);
	}
	
	private BooleanFlagComponent<T> parseBooleanFlag(String value, ComponentFactory<T> componentFactory) {
		String[] split = value.split(",");
		if (Arrays.stream(split).anyMatch(s -> !s.startsWith("-"))) {
			throw new IllegalArgumentException("All flag aliases must begin with a dash");
		}
		return componentFactory.createBooleanFlag(split);
	}

	private ContextProvider<T, ?> getDefaultValue(String name, ArgType<T, ?> type, String str, ParserOptions<T> options) {
		if (str.startsWith("context ")) {
			return options.getContextProvider(str.substring(8));
		}
		return ContextProvider.create(null, "Failed to parse default value for " + name, ctx -> type.convert(ctx, str));
	}

	private String trimToken(Token token) {
		return token.getBaseString().substring(token.getStart() + 1, token.getEnd() - 1);
	}

	private static <T> T getDirect(Map<String, List<T>> map, String key) {
		return map.get(key).get(0);
	}

	private static <T> Optional<T> getOptional(Map<String, List<T>> map, String key) {
		List<T> list = map.get(key);
		if (list == null || list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(list.get(0));
	}

}
