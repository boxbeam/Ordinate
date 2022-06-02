package redempt.ordinate.component.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.processing.MessageFormatter;

public class OptionalArgumentComponent<T, V> extends ArgumentComponent<T, V> {

	private ContextProvider<T, V> defaultValue;

	public OptionalArgumentComponent(String name, ArgType<T, V> type, ContextProvider<T, V> defaultValue, MessageFormatter<T> invalidError) {
		super(name, type, null, invalidError);
		this.defaultValue = defaultValue;
	}

	@Override
	public int getMinConsumedArgs() {
		return 0;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		int minWidth = context.getCommand().getPipeline().getMinArgWidth();
		int providedArgs = context.initialArgCount();
		if (!context.hasArg() || providedArgs <= minWidth) {
			context.setParsed(getIndex(), defaultValue.provide(context));
			return success();
		}
		String value = context.peekArg().getValue();
		V parsed = getType().convert(context, value);
		if (parsed == null) {
			return failure(getInvalidError().apply(context.sender(), getName(), value)).complete();
		}
		context.pollArg();
		context.setParsed(getIndex(), parsed);
		context.provide(parsed);
		return success();
	}

	@Override
	public HelpComponent getHelpComponent() {
		return new HelpComponent(this, 1, "[" + getName() + "]");
	}
}
