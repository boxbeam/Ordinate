package redempt.ordinate.command;

import redempt.ordinate.component.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;

public class Command<T> extends CommandComponent<T> {
	
	@Override
	public int getPriority() {
		return 0;
	}
	
	@Override
	public boolean canParse(CommandContext<T> context) {
		return false;
	}
	
	@Override
	public CommandResult<T> parse() {
		return null;
	}
	
}
