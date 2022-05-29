package redempt.ordinate.context;

import redempt.ordinate.data.CommandContext;

public interface ContextProvider<T, V> {

	public V provide(CommandContext<T> context);

}
