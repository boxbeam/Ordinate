package redempt.ordinate.argument;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.processing.MessageFormatter;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.help.LiteralHelpComponent;

import java.util.Set;

public class ArgumentComponent<T, V> extends CommandComponent<T> implements Named, HelpProvider {

	private String name;
	private ArgType<T, V> type;
	private MessageFormatter<T> missingError;
	private MessageFormatter<T> invalidError;

	public ArgumentComponent(String name, ArgType<T, V> type, MessageFormatter<T> missingError, MessageFormatter<T> invalidError) {
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
	public HelpComponent getHelpComponent() {
		return new LiteralHelpComponent(this, 1, false, "<" + name + ">");
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (!context.hasArg()) {
			return failure(missingError.apply(context.sender(), name));
		}
		String arg = context.pollArg().getValue();
		V val = type.convert(context, arg);
		if (val != null) {
			context.setParsed(getIndex(), val);
			return success();
		}
		return failure(invalidError.apply(context.sender(), name, arg)).complete();
	}

	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		if (context.getArguments().size() > 1) {
			return parse(context);
		}
		String partial = context.peekArg().getValue();
		completions.addAll(type.complete(context, partial));
		return success();
	}

	@Override
	public String getName() {
		return name;
	}
}
