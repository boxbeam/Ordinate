package redempt.ordinate.command;

import redempt.ordinate.data.*;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpEntry;
import redempt.ordinate.help.HelpPage;
import redempt.ordinate.processing.ArgumentSplitter;

import java.util.*;
import java.util.stream.Collectors;

public class CommandBase<T> implements Named {

	private List<Command<T>> wrapped;
	private HelpPage help;
	private CommandManager<T> manager;

	public CommandBase(List<Command<T>> wrapped, CommandManager<T> manager) {
		this.wrapped = wrapped;
		HelpBuilder helpBuilder = new HelpBuilder();
		wrapped.forEach(cmd -> cmd.addHelp(helpBuilder));
		help = helpBuilder.build();
		this.manager = manager;
	}

	public HelpPage getHelpPage() {
		return help;
	}

	public List<Command<T>> getCommands() {
		return wrapped;
	}

	@Override
	public String getName() {
		return wrapped.get(0).getName();
	}
	
	public List<String> getNames() {
		return wrapped.stream().flatMap(c -> c.getNames().stream()).distinct().collect(Collectors.toList());
	}
	
	public CompletionResult<T> getCompletions(T sender, String[] args) {
		return getCompletions(sender, ArgumentSplitter.split(args, true));
	}

	public CompletionResult<T> getCompletions(T sender, String args) {
		return getCompletions(sender, ArgumentSplitter.split(args, true));
	}

	public CompletionResult<T> getCompletions(T sender, SplittableList<Argument> args) {
		Set<String> completions = new LinkedHashSet<>();
		CommandResult<T> deepestError = null;
		for (Command<T> cmd : wrapped) {
			CommandContext<T> context = cmd.createContext(sender, args);
			CommandResult<T> result = cmd.complete(context, completions);
			if (!result.isSuccess()) {
				deepestError = CommandResult.deepest(deepestError, result);
			}
		}
		return new CompletionResult<>("", deepestError, completions);
	}

	public CommandResult<T> execute(T sender, String args) {
		return execute(sender, ArgumentSplitter.split(args, false));
	}

	public CommandResult<T> execute(T sender, String[] args) {
		return execute(sender, ArgumentSplitter.split(args, false));
	}

	public CommandResult<T> execute(T sender, SplittableList<Argument> args) {
		CommandResult<T> deepestError = null;
		for (Command<T> cmd : wrapped) {
			CommandContext<T> context = cmd.createContext(sender, args);
			CommandResult<T> result = cmd.parse(context);
			if (result.isSuccess()) {
				return result;
			}
			deepestError = CommandResult.deepest(deepestError, result);
		}
		if (deepestError != null) {
			
			deepestError.getError().send(sender);
			if (deepestError.getComponent() instanceof Command) {
				manager.getHelpDisplayer().display(sender, help.getHelpRecursive(deepestError.getCommand()));
			} else {
				manager.getHelpDisplayer().display(sender, help.getHelp(deepestError.getCommand()));
			}
			return deepestError;
		}
		HelpEntry[] all = help.getAll().toArray(new HelpEntry[0]);
		manager.getHelpDisplayer().display(sender, all);
		return null;
	}

}
