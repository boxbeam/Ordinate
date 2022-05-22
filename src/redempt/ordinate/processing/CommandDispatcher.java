package redempt.ordinate.processing;

import redempt.ordinate.data.CommandContext;

public interface CommandDispatcher<T> {
	
	public void dispatch(CommandContext<T> context);
	
}
