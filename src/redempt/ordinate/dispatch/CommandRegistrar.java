package redempt.ordinate.dispatch;

import redempt.ordinate.command.CommandBase;

public interface CommandRegistrar<T> {

	public void register(CommandBase<T> command);
	public void unregister(CommandBase<T> command);

}
