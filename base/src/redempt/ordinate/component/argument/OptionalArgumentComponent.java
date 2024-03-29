package redempt.ordinate.component.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.message.MessageFormatter;

public class OptionalArgumentComponent<T, V> extends ArgumentComponent<T, V> {

	private ContextProvider<T, V> defaultValue;
	private MessageFormatter<T> contextError;

	public OptionalArgumentComponent(String name, ArgType<T, V> type, ContextProvider<T, V> defaultValue,
	                                 MessageFormatter<T> invalidError, MessageFormatter<T> contextError) {
		super(name, type, null, invalidError);
		this.defaultValue = defaultValue;
		this.contextError = contextError;
	}

	@Override
	public boolean isOptional() {
		return true;
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
			return handleDefault(context);
		}
		String value = context.peekArg().getValue();
		V parsed = getType().convert(context, value);
		if (parsed == null) {
			CommandResult<T> defaultResult = handleDefault(context);
			return defaultResult.isSuccess() ? failure(getInvalidError().format(context.sender(), getName(), value)) : defaultResult;
		}
		context.pollArg();
		context.setParsed(getIndex(), parsed);
		context.provide(parsed);
		return success();
	}
	
	private CommandResult<T> handleDefault(CommandContext<T> context) {
		if (defaultValue == null) {
			return success();
		}
		V value = defaultValue.provide(context);
		if (value == null) {
			return failure(contextError.format(context.sender(), defaultValue.getError())).complete();
		}
		context.setParsed(getIndex(), value);
		context.provide(value);
		return success();
	}

	@Override
	public void addHelp(HelpBuilder<T> help) {
		help.addHelp(new HelpComponent(this, 1, "[" + getName() + "]"));
	}
}
