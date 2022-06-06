package redempt.ordinate.command;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.help.HelpPage;

import java.util.Collection;
import java.util.List;

public class CommandBase<T> {

	private List<Command<T>> wrapped;
	private HelpPage help;
	private HelpDisplayer<T> helpDisplayer;

	public CommandBase(List<Command<T>> wrapped, HelpDisplayer<T> helpDisplayer) {
		this.wrapped = wrapped;
		HelpBuilder helpBuilder = new HelpBuilder();
		wrapped.forEach(cmd -> cmd.addHelp(helpBuilder));
		help = helpBuilder.build();
		this.helpDisplayer = helpDisplayer;
	}

	public HelpPage getHelpPage() {
		return help;
	}

	public List<Command<T>> getCommands() {
		return wrapped;
	}

	public Collection<String> getCompletions(T sender, String[] args) {
		// todo
		return null;
	}

	public boolean execute(T sender, String[] args) {
		CommandResult<T> deepestError = null;
		for (Command<T> cmd : wrapped) {
			CommandContext<T> context = cmd.createContext(sender, args, false);
			CommandResult<T> result = cmd.parse(context);
			if (result.isSuccess()) {
				return true;
			}
			deepestError = CommandResult.deepest(deepestError, result);
		}
		// todo
		return false;
	}

}
