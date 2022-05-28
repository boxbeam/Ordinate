package redempt.ordinate.creation;

import redempt.ordinate.component.argument.ArgType;
import redempt.ordinate.component.argument.ArgumentComponent;

public interface ComponentFactory<T> {

	public <V> ArgumentComponent<T, V> createArgument(ArgType<T, V> type, String name);

}
