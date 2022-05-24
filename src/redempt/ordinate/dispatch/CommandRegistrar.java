package redempt.ordinate.dispatch;

import redempt.ordinate.command.Command;

public interface CommandRegistrar<T> {

	public void register(Command<T> command);

}
