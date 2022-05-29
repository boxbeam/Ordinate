package redempt.ordinate.component.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.processing.MessageFormatter;

import java.util.StringJoiner;

public class ConsumingArgumentComponent<T, V> extends ArgumentComponent<T, V> {

	private boolean optional;
	private ContextProvider<T, V> defaultValue;

	public ConsumingArgumentComponent(String name, ArgType<T, V> type, boolean optional, ContextProvider<T, V> defaultValue, MessageFormatter<T> missingError, MessageFormatter<T> invalidError) {
		super(name, type, missingError, invalidError);
		this.defaultValue = defaultValue;
		this.optional = optional;
	}

	public boolean isOptional() {
		return optional;
	}

	@Override
	public int getMaxConsumedArgs() {
		return Integer.MAX_VALUE;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (!context.hasArg() && !optional) {
			return failure(getMissingError().apply(context.sender(), getName())).complete();
		}
		if (!context.hasArg()) {
			context.setParsed(getIndex(), defaultValue.provide(context));
			context.provide(defaultValue);
		}
		StringJoiner joiner = new StringJoiner(" ");
		while (context.hasArg()) {
			joiner.add(context.pollArg().getValue());
		}
		String value = joiner.toString();
		V parsed = getType().convert(context, value);
		if (parsed == null) {
			return failure(getInvalidError().apply(context.sender(), getName(), value)).complete();
		}
		context.setParsed(getIndex(), parsed);
		context.provide(parsed);
		return success();
	}

}
