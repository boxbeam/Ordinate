package redempt.ordinate.context;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.message.MessageFormatter;

public class ContextComponent<T, V> extends CommandComponent<T> implements Named {
	
	private String name;
	private ContextProvider<T, V> contextProvider;
	private MessageFormatter<T> error;
	
	public ContextComponent(String name, ContextProvider<T, V> contextProvider, MessageFormatter<T> error) {
		this.name = name;
		this.contextProvider = contextProvider;
		this.error = error;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMaxConsumedArgs() {
		return 0;
	}

	@Override
	public int getMaxParsedObjects() {
		return 1;
	}

	@Override
	public int getPriority() {
		return 10;
	}
	
	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		V value = contextProvider.provide(context);
		if (value != null) {
			context.setParsed(getIndex(), value);
			context.provide(value);
			return success();
		}
		return failure(error.format(context.sender(), contextProvider.getError())).complete();
	}
	
}
