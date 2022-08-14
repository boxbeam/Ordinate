package redempt.ordinate.command;

import redempt.ordinate.component.HelpSubcommandComponent;
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
	private HelpPage<T> help;
	private CommandManager<T> manager;

	public CommandBase(List<Command<T>> wrapped, CommandManager<T> manager) {
		this.wrapped = wrapped;
		HelpBuilder<T> helpBuilder = new HelpBuilder<>();
		wrapped.forEach(cmd -> cmd.addHelp(helpBuilder));
		help = helpBuilder.build();
		this.manager = manager;
	}

	public HelpPage<T> getHelpPage() {
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
		String last = args.hasNext() ? args.get(args.size() - 1).getValue() : "";
		List<String> fixed = completions.stream()
				.filter(s -> s.regionMatches(true, 0, last, 0, last.length()))
				.map(s -> s.contains(" ") ? '"' + s.replace("\"", "\\\"") + '"' : s).collect(Collectors.toList());
		return new CompletionResult<>(deepestError, fixed);
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
		deepestError.getError().send(sender);
		if (deepestError.getComponent() instanceof Command || deepestError.getComponent() instanceof HelpSubcommandComponent) {
			manager.getHelpDisplayer().display(sender, help.getHelpRecursive(deepestError.getCommand(), true));
		} else {
			manager.getHelpDisplayer().display(sender, help.getHelp(deepestError.getCommand()));
		}
		return deepestError;
	}

}
