package redempt.ordinate.component.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.message.MessageFormatter;

import java.util.StringJoiner;

public class ConsumingArgumentComponent<T, V> extends ArgumentComponent<T, V> {

	private boolean optional;
	private ContextProvider<T, V> defaultValue;
	private MessageFormatter<T> contextError;

	public ConsumingArgumentComponent(String name, ArgType<T, V> type, boolean optional, ContextProvider<T, V> defaultValue,
	                                  MessageFormatter<T> missingError, MessageFormatter<T> invalidError, MessageFormatter<T> contextError) {
		super(name, type, missingError, invalidError);
		this.defaultValue = defaultValue;
		this.optional = optional;
		this.contextError = contextError;
	}

	@Override
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
			return failure(getMissingError().format(context.sender(), getName())).complete();
		}
		if (!context.hasArg()) {
			V value = defaultValue.provide(context);
			if (value == null) {
				return failure(contextError.format(context.sender(), defaultValue.getError())).complete();
			}
			context.setParsed(getIndex(), value);
			context.provide(value);
		}
		StringJoiner joiner = new StringJoiner(" ");
		while (context.hasArg()) {
			joiner.add(context.pollArg().getValue());
		}
		String value = joiner.toString();
		V parsed = getType().convert(context, value);
		if (parsed == null) {
			return failure(getInvalidError().format(context.sender(), getName(), value)).complete();
		}
		context.setParsed(getIndex(), parsed);
		context.provide(parsed);
		return success();
	}

	@Override
	public void addHelp(HelpBuilder<T> help) {
		help.addHelp(new HelpComponent(this, 1, "<" + getName() + ">+"));
	}

}
