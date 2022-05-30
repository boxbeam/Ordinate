package redempt.ordinate.parser.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.constraint.Constraint;
import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.parser.metadata.ParserOptions;
import redempt.ordinate.processing.CommandParsingPipeline;

import java.util.Arrays;

public class DefaultArgumentParser<T> implements ArgumentParser<T> {

	@Override
	public <V> void parseArgument(String argument, ParserOptions<T> options, ComponentFactory<T> componentFactory, CommandParsingPipeline<T> pipeline) {
		if (argument.startsWith("-")) {
			String[] split = argument.split(",");
			if (Arrays.stream(split).anyMatch(s -> !s.startsWith("-"))) {
				throw new IllegalArgumentException("All flag aliases must begin with a dash");
			}
			pipeline.addComponent(componentFactory.createBooleanFlag(split));
			return;
		}
		boolean optional = false;
		boolean consume = false;
		boolean vararg = false;
		ContextProvider<T, V> defaultValue = null;
		Constraint<T, ?> constraint = null;
		String constraintString = null;
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
		if (typeName.endsWith(">")) {
			int begin = typeName.indexOf('<');
			constraintString = typeName.substring(begin + 1, typeName.length() - 1);
			typeName = typeName.substring(0, begin);
		}
		ArgType<T, V> type = (ArgType<T, V>) options.getArgType(typeName);
		if (constraintString != null) {
			ConstraintParser<T, V> constraintParser = type.getConstraintParser();
			if (constraintParser == null) {
				throw new IllegalArgumentException("No constraint parser for type " + typeName + ", cannot handle constraint value " + constraintString);
			}
			constraint = constraintParser.parse(constraintString);
		}
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
			pipeline.addComponent(componentFactory.createConsumingArgument(type, optional, defaultValue, name));
		} else if (vararg) {
			pipeline.addComponent(componentFactory.createVariableLengthArgument(type, optional, name));
			if (constraint != null) {
				constraint = Constraint.arrayConstraint(constraint);
			}
		} else if (optional) {
			pipeline.addComponent(componentFactory.createOptionalArgument(type, defaultValue, name));
		} else {
			pipeline.addComponent(componentFactory.createArgument(type, name));
		}
		if (constraint != null) {
			pipeline.addComponent(componentFactory.createConstraint(constraint, name));
		}
	}

}
