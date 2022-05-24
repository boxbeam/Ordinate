package redempt.ordinate.dispatch;

import redempt.ordinate.data.CommandContext;

public interface CommandDispatcher<T> {
	
	public void dispatch(CommandContext<T> parsed);
	
}
