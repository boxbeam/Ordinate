package redempt.ordinate.creation;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.BooleanFlagComponent;
import redempt.ordinate.component.SubcommandLookupComponent;
import redempt.ordinate.component.argument.*;
import redempt.ordinate.constraint.Constraint;
import redempt.ordinate.constraint.ConstraintComponent;
import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.constraint.NumberConstraint;
import redempt.ordinate.context.ContextComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.dispatch.DispatchComponent;
import redempt.ordinate.message.MessageFormatter;
import redempt.ordinate.message.MessageProvider;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class DefaultComponentFactory<T> implements ComponentFactory<T> {

	private MessageProvider<T> messages;

	public DefaultComponentFactory(MessageProvider<T> messages) {
		this.messages = messages;
	}
	
	private MessageFormatter<T> getMessage(String name) {
		return messages.getFormatter(name);
	}
	
	@Override
	public <V> ArgumentComponent<T, V> createArgument(ArgType<T, V> type, String name) {
		return new ArgumentComponent<>(name, type, getMessage("missingArgument"), getMessage("invalidArgumentValue"));
	}

	@Override
	public <V> OptionalArgumentComponent<T, V> createOptionalArgument(ArgType<T, V> type, ContextProvider<T, V> defaultValue, String name) {
		return new OptionalArgumentComponent<>(name, type, defaultValue, getMessage("invalidArgumentValue"), getMessage("contextError"));
	}

	@Override
	public <V> ConsumingArgumentComponent<T, V> createConsumingArgument(ArgType<T, V> type, boolean optional, ContextProvider<T, V> defaultValue, String name) {
		return new ConsumingArgumentComponent<>(name, type, optional, defaultValue, getMessage("missingArgument"), getMessage("invalidArgumentValue"), getMessage("contextError"));
	}

	@Override
	public <V> VariableLengthArgumentComponent<T, V> createVariableLengthArgument(ArgType<T, V> type, boolean optional, String name) {
		return new VariableLengthArgumentComponent<>(name, type, optional, getMessage("missingArgument"), getMessage("invalidArgumentValue"));
	}

	@Override
	public BooleanFlagComponent<T> createBooleanFlag(String... names) {
		String primaryName = names[0];
		Set<String> allNames = new HashSet<>();
		Collections.addAll(allNames, names);
		return new BooleanFlagComponent<>(primaryName, allNames);
	}

	@Override
	public <V> ContextComponent<T, V> createContext(ContextProvider<T, V> provider, String name) {
		return new ContextComponent<>(name, provider, getMessage("contextError"));
	}

	@Override
	public DispatchComponent<T> createDispatch(CommandDispatcher<T> dispatcher) {
		return new DispatchComponent<>(dispatcher, getMessage("tooManyArguments"));
	}

	@Override
	public <V> ConstraintComponent<T, V> createConstraint(Constraint<T, V> constraint, Supplier<Integer> index, String name) {
		return new ConstraintComponent<>(constraint, index, name, getMessage("constraintError"));
	}

	@Override
	public SubcommandLookupComponent<T> createLookupComponent(List<Command<T>> commands) {
		return new SubcommandLookupComponent<>(commands, getMessage("invalidSubcommand"));
	}

	@Override
	public <V extends Number & Comparable<V>> ConstraintParser<T, V> createNumberConstraintParser(Function<String, V> parseNumber) {
		return NumberConstraint.createParser(parseNumber, getMessage("numberOutsideRange"));
	}

}
