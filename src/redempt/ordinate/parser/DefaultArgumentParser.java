package redempt.ordinate.parser;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;

import java.util.Arrays;

public class DefaultArgumentParser<T> implements ArgumentParser<T> {

	@Override
	public <V> CommandComponent<T> parseArgument(String argument, ParserOptions<T> options, ComponentFactory<T> componentFactory) {
		if (argument.startsWith("-")) {
			String[] split = argument.split(",");
			if (Arrays.stream(split).anyMatch(s -> !s.startsWith("-"))) {
				throw new IllegalArgumentException("All flag aliases must begin with a dash");
			}
			return componentFactory.createBooleanFlag(split);
		}
		boolean optional = false;
		boolean consume = false;
		boolean vararg = false;
		ContextProvider<T, V> defaultValue = null;
		String[] split = argument.split(":", 2);
		String typeName = split[0];
		if (typeName.endsWith("...")) {
			consume = true;
			typeName = typeName.substring(0, typeName.length() - 3);
		}
		if (typeName.endsWith("[]")) {
			vararg = true;
			typeName = typeName.substring(0, typeName.length() - 2);
		}
		ArgType<T, V> type = (ArgType<T, V>) options.getArgType(typeName);
		String name = split[1];
		if (name.endsWith("?")) {
			optional = true;
			name = name.substring(0, name.length() - 1);
		}
		if (name.endsWith(")")) {
			optional = true;
			int start = name.indexOf('(');
			name = name.substring(0, start - 1);
			String defaultString = name.substring(start + 1, name.length() - 1);
			if (defaultString.startsWith("context ")) {
				String contextName = defaultString.substring(8);
				defaultValue = (ContextProvider<T, V>) options.getContextProvider(contextName);
			} else {
				defaultValue = ContextProvider.create(null, "Failed to parse default value for " + name, ctx -> type.convert(ctx, defaultString));
			}
		}
		if (consume) {
			return componentFactory.createConsumingArgument(type, optional, defaultValue, name);
		}
		if (vararg) {
			return componentFactory.createVariableLengthArgument(type, optional, name);
		}
		if (optional) {
			return componentFactory.createOptionalArgument(type, defaultValue, name);
		}
		return componentFactory.createArgument(type, name);
	}

}
