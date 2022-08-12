package redempt.ordinate.component;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.Argument;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.message.MessageFormatter;

import java.util.*;

public class SubcommandLookupComponent<T> extends CommandComponent<T> implements HelpProvider {

	private List<Command<T>> commands;
	private Map<String, List<Command<T>>> lookup = new HashMap<>();
	private MessageFormatter<T> invalidSubcommand;

	public SubcommandLookupComponent(List<Command<T>> commands, MessageFormatter<T> invalidSubcommand) {
		this.commands = commands;
		this.invalidSubcommand = invalidSubcommand;
		for (Command<T> command : commands) {
			command.getNames().forEach(name -> lookup.computeIfAbsent(name, k -> new ArrayList<>()).add(command));
		}
	}

	@Override
	public void setParent(Command<T> parent) {
		super.setParent(parent);
		commands.forEach(c -> c.setParent(parent));
	}

	@Override
	public int getMinConsumedArgs() {
		return 0;
	}

	@Override
	public int getMaxConsumedArgs() {
		return 0;
	}

	@Override
	public int getMaxParsedObjects() {
		return 0;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (!context.hasArg()) {
			return success();
		}
		Argument arg = context.peekArg();
		if (arg.isQuoted() || !lookup.containsKey(arg.getValue())) {
			return success();
		}
		List<Command<T>> subcommands = lookup.get(arg.getValue());
		CommandResult<T> deepestError = null;
		for (Command<T> command : subcommands) {
			CommandResult<T> result = command.parse(context.clone(command, 0, command.getMaxParsedObjects()));
			if (result.isSuccess() && result.isComplete()) {
				return result;
			}
			if (!result.isSuccess()) {
				deepestError = CommandResult.deepest(deepestError, result);
			}
		}
		return deepestError == null ? failure(invalidSubcommand.format(context.sender(), arg.getValue())) : deepestError;
	}

	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		if (context.getArguments().size() > 1) {
			Argument arg = context.peekArg();
			if (arg.isQuoted() || !lookup.containsKey(arg.getValue())) {
				return success();
			}
			List<Command<T>> subcommands = lookup.get(arg.getValue());
			for (Command<T> command : subcommands) {
				command.complete(context.clone(command, 0, command.getMaxParsedObjects()), completions);
			}
			return success();
		}
		completions.addAll(lookup.keySet());
		return success();
	}

	@Override
	public void addHelp(HelpBuilder help) {
		commands.forEach(c -> c.addHelp(help));
	}
}
