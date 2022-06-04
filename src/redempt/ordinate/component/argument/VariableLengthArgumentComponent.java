package redempt.ordinate.component.argument;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.processing.MessageFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VariableLengthArgumentComponent<T, V> extends ArgumentComponent<T, V> {

	private boolean optional;

	public VariableLengthArgumentComponent(String name, ArgType<T, V> type, boolean optional, MessageFormatter<T> missingError, MessageFormatter<T> invalidError) {
		super(name, type, missingError, invalidError);
		this.optional = optional;
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
	public int getMinConsumedArgs() {
		return optional ? 0 : 1;
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
	public CommandResult<T> parse(CommandContext<T> context) {
		List<V> list = new ArrayList<>();
		boolean hasNext = context.hasArg();
		while (context.hasArg()) {
			String value = context.peekArg().getValue();
			V parsed = getType().convert(context, value);
			if (parsed == null) {
				break;
			}
			context.pollArg();
			list.add(parsed);
		}
		if (list.size() == 0 && !optional) {
			if (hasNext) {
				return failure(getInvalidError().apply(context.sender(), getName(), context.peekArg().getValue())).complete();
			}
			return failure(getMissingError().apply(context.sender(), getName())).complete();
		}
		context.setParsed(getIndex(), list);
		return success();
	}

	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		parse(context);
		if (context.getArguments().size() <= 1) {
			completions.addAll(getType().complete(context, context.peekArg().getValue()));
		}
		return success();
	}

	@Override
	public void addHelp(HelpBuilder help) {
		help.addHelp(new HelpComponent(this, 1, "<" + getName() + ">+"));
	}

}
