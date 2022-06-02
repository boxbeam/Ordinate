package redempt.ordinate.component.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.processing.MessageFormatter;
import redempt.ordinate.help.HelpComponent;

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

	public boolean isOptional() {
		return false;
	}

	public MessageFormatter<T> getInvalidError() {
		return invalidError;
	}

	public MessageFormatter<T> getMissingError() {
		return missingError;
	}

	public ArgType<T, V> getType() {
		return type;
	}

	public void setType(ArgType<T, ?> type) {
		this.type = (ArgType<T, V>) type;
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
		return new HelpComponent(this, 1, "<" + name + ">");
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (!context.hasArg()) {
			return failure(missingError.apply(context.sender(), name)).complete();
		}
		String arg = context.pollArg().getValue();
		V val = type.convert(context, arg);
		if (val != null) {
			context.setParsed(getIndex(), val);
			context.provide(val);
			return success();
		}
		return failure(invalidError.apply(context.sender(), name, arg)).complete();
	}

	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		if (context.getArguments().size() != 1) {
			return parse(context);
		}
		String partial = context.pollArg().getValue();
		completions.addAll(type.complete(context, partial));
		return success();
	}

	@Override
	public String getName() {
		return name;
	}
}
