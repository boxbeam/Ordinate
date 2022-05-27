package redempt.ordinate.dispatch;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.processing.MessageFormatter;

import java.util.Set;

public class CommandDispatchComponent<T> extends CommandComponent<T> {

	private CommandDispatcher<T> dispatcher;
	private MessageFormatter errorGenerator;

	@Override
	public int getMaxConsumedArgs() {
		return 0;
	}

	@Override
	public int getMaxParsedObjects() {
		return 0;
	}

	@Override
	public int getPriority() {
		return Integer.MIN_VALUE;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {

		return null;
	}

	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		return success();
	}
}
