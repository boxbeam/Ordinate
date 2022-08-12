package redempt.ordinate.dispatch;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.message.MessageFormatter;

import java.util.Set;

public class DispatchComponent<T> extends CommandComponent<T> {

	private CommandDispatcher<T> dispatcher;
	private MessageFormatter<T> error;
	private MessageFormatter<T> tooManyArgsError;

	public DispatchComponent(CommandDispatcher<T> dispatcher, MessageFormatter<T> error, MessageFormatter<T> tooManyArgsError) {
		this.dispatcher = dispatcher;
		this.error = error;
		this.tooManyArgsError = tooManyArgsError;
	}

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
		if (context.getArguments().size() > 0) {
			int leftover = context.initialArgCount() - context.getArguments().size();
			return failure(tooManyArgsError.format(context.sender(), Integer.toString(leftover))).complete();
		}
		try {
			dispatcher.dispatch(context);
			return success().complete();
		} catch (Exception e) {
			e.printStackTrace();
			return failure(error.format(context.sender())).complete();
		}
	}

	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		return success();
	}
}
