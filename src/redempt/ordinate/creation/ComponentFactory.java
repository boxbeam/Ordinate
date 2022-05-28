package redempt.ordinate.creation;

import redempt.ordinate.component.BooleanFlagComponent;
import redempt.ordinate.component.argument.*;

public interface ComponentFactory<T> {

	public <V> ArgumentComponent<T, V> createArgument(ArgType<T, V> type, String name);
	public <V> OptionalArgumentComponent<T, V> createOptionalArgument(ArgType<T, V> type, V defaultValue, String name);
	public <V> ConsumingArgumentComponent<T, V> createConsumingArgument(ArgType<T, V> type, boolean optional, V defaultValue, String name);
	public <V> VariableLengthArgumentComponent<T, V> createVariableLengthArgument(ArgType<T, V> type, boolean optional, String name);
	public BooleanFlagComponent<T> createBooleanFlag(String... names);

}
