package redempt.ordinate.argument;

import redempt.ordinate.component.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.processing.Formatter;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.help.LiteralHelpComponent;

public class ArgumentComponent<T, V> extends CommandComponent<T> implements Named {

	private String name;
	private ArgType<T, V> type;
	private Formatter missingError;
	private Formatter invalidError;

	public ArgumentComponent(String name, ArgType<T, V> type, Formatter missingError, Formatter invalidError) {
		this.name = name;
		this.type = type;
		this.missingError = missingError;
		this.invalidError = invalidError;
	}

	@Override
	public int getMaxConsumedArgs() {
		return 1;
	}

	@Override
	public int getMaxParsedObjects() {
		return 1;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public HelpComponent getHelpDisplay() {
		return new LiteralHelpComponent(this, 1, "<" + name + ">");
	}

	@Override
	public boolean canParse(CommandContext<T> context) {
		return true;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (!context.hasArg()) {
			return failure(missingError.apply(name));
		}
		String arg = context.pollArg().getValue();
		V val = type.convert(context, arg);
		if (val != null) {
			context.setParsed(getIndex(), val);
			return success();
		}
		return failure(invalidError.apply(name, arg));
	}

	@Override
	public String getName() {
		return name;
	}
}
