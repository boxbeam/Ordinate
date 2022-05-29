package redempt.ordinate.creation;

import redempt.ordinate.component.BooleanFlagComponent;
import redempt.ordinate.component.argument.*;
import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.context.ContextComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.dispatch.DispatchComponent;
import redempt.ordinate.processing.MessageFormatter;

import java.util.function.Function;

public interface ComponentFactory<T> {

	public <V> ArgumentComponent<T, V> createArgument(ArgType<T, V> type, String name);
	public <V> OptionalArgumentComponent<T, V> createOptionalArgument(ArgType<T, V> type, ContextProvider<T, V> defaultValue, String name);
	public <V> ConsumingArgumentComponent<T, V> createConsumingArgument(ArgType<T, V> type, boolean optional, ContextProvider<T, V> defaultValue, String name);
	public <V> VariableLengthArgumentComponent<T, V> createVariableLengthArgument(ArgType<T, V> type, boolean optional, String name);
	public BooleanFlagComponent<T> createBooleanFlag(String... names);
	public <V> ContextComponent<T, V> createContext(ContextProvider<T, V> provider, String name, MessageFormatter<T> error);
	public DispatchComponent<T> createDispatch(CommandDispatcher<T> dispatcher);
	public <V extends Number & Comparable<V>> ConstraintParser<T, V> createNumberConstraintParser(Function<String, V> parseNumber);

}
