package redempt.ordinate.processing;

import redempt.ordinate.command.Command;

public interface TagProcessor<T> {

	public String getName();
	public Command<T> apply(Command<T> cmd, String value);

}
