package redempt.ordinate.context;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.message.MessageFormatter;

public class AssertComponent<T> extends CommandComponent<T> {
	
	private ContextProvider<T, ?> contextProvider;
	private MessageFormatter<T> contextError;
	
	public AssertComponent(ContextProvider<T, ?> contextProvider, MessageFormatter<T> contextError) {
		this.contextProvider = contextProvider;
		this.contextError = contextError;
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
		return 10;
	}
	
	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (contextProvider.provide(context) == null) {
			return failure(contextError.format(context.sender(), contextProvider.getError())).complete();
		}
		return success();
	}
	
}
