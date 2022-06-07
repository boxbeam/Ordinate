package redempt.ordinate.command;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.help.HelpEntry;
import redempt.ordinate.help.HelpPage;

import java.util.Collection;
import java.util.List;

public class CommandBase<T> {

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

	public Collection<String> getCompletions(T sender, String[] args) {
		return getCompletions(sender, String.join(" ", args));
	}

	public Collection<String> getCompletions(T sender, String args) {
		// todo
		return null;
	}

	public boolean execute(T sender, String[] args) {
		return execute(sender, String.join(" ", args));
	}

	public boolean execute(T sender, String args) {
		CommandResult<T> deepestError = null;
		for (Command<T> cmd : wrapped) {
			CommandContext<T> context = cmd.createContext(sender, args, false);
			CommandResult<T> result = cmd.parse(context);
			if (result.isSuccess()) {
				return true;
			}
			deepestError = CommandResult.deepest(deepestError, result);
		}
		if (deepestError != null) {
			manager.getMessageDispatcher().sendMessage(sender, deepestError.getError());
			if (deepestError.getComponent() instanceof Command) {

				manager.getHelpDisplayer().display(sender, help.getHelpRecursive(deepestError.getCommand()));
			} else {
				manager.getHelpDisplayer().display(sender, help.getHelp(deepestError.getCommand()));
			}
			return false;
		}
		HelpEntry[] all = help.getAll().toArray(new HelpEntry[0]);
		manager.getHelpDisplayer().display(sender, all);
		return false;
	}

}
