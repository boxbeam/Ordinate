package redempt.ordinate.component.flag;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.data.*;
import redempt.ordinate.message.MessageFormatter;

import java.util.Set;

public class FlagComponent<T, V> extends CommandComponent<T> implements Named {
	
	private Set<String> names;
	private String mainName;
	private ArgType<T, V> type;
	private ContextProvider<T, V> defaultValue;
	private MessageFormatter<T> invalidArgument;
	private MessageFormatter<T> contextError;
	private MessageFormatter<T> noArgument;
	
	public FlagComponent(String mainName, Set<String> names, ArgType<T, V> type, ContextProvider<T, V> defaultValue, MessageFormatter<T> invalidArgument, MessageFormatter<T> contextError, MessageFormatter<T> noArgument) {
		this.names = names;
		this.mainName = mainName;
		this.type = type;
		this.invalidArgument = invalidArgument;
		this.contextError = contextError;
		this.noArgument = noArgument;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public int getMaxConsumedArgs() {
		return 2;
	}
	
	@Override
	public int getMaxParsedObjects() {
		return 1;
	}
	
	@Override
	public int getMinConsumedArgs() {
		return 0;
	}
	
	@Override
	public int getPriority() {
		return 5;
	}
	
	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		SplittableList<Argument> arguments = context.getArguments();
		for (int i = 0; i < arguments.size(); i++) {
			Argument arg = arguments.get(i);
			String value = arg.getValue();
			if (arg.isQuoted()) {
				continue;
			}
			if (!names.contains(value)) {
				continue;
			}
			context.removeArg(i, true);
			return handleFlag(context, i);
		}
		return handleDefault(context);
	}
	
	private int find(CommandContext<T> context) {
		for (int i = 0; i < context.getArguments().size(); i++) {
			Argument arg = context.getArguments().get(i);
			if (!arg.isQuoted() && names.contains(arg.getValue())) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		int pos = find(context);
		int argCount = context.getArguments().size();
		if (pos != -1 && pos < argCount - 2) {
			parse(context);
			return success();
		}
		if (argCount == 1) {
			Argument arg = context.peekArg();
			if (arg.isQuoted() || !arg.getValue().startsWith("-")) {
				return success();
			}
			completions.add(getName());
		} else if (argCount == 2) {
			completions.addAll(type.complete(context, context.getArguments().get(argCount - 1).getValue()));
		}
		return success();
	}
	
	private CommandResult<T> handleDefault(CommandContext<T> context) {
		if (defaultValue == null) {
			return success();
		}
		V val = defaultValue.provide(context);
		if (val == null) {
			return failure(contextError.format(context.sender(), defaultValue.getError())).complete();
		}
		context.setParsed(getIndex(), val);
		return success();
	}
	
	private CommandResult<T> handleFlag(CommandContext<T> context, int i) {
		if (i >= context.getArguments().size()) {
			return failure(noArgument.format(context.sender(), getName())).complete();
		}
		String next = context.getArguments().get(i).getValue();
		context.removeArg(i, true);
		V val = type.convert(context, next);
		if (val == null) {
			return failure(invalidArgument.format(context.sender(), getName())).complete();
		}
		context.setParsed(getIndex(), val);
		return success();
	}
	
	@Override
	public String getName() {
		return mainName;
	}
	
}
