package redempt.ordinate.command;

import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpPage;

import java.util.Collection;
import java.util.List;

public class CommandBase<T> {

	private List<Command<T>> wrapped;
	private HelpPage help;

	public CommandBase(List<Command<T>> wrapped) {
		this.wrapped = wrapped;
		HelpBuilder helpBuilder = new HelpBuilder();
		wrapped.forEach(cmd -> cmd.addHelp(helpBuilder));
		help = helpBuilder.build();
	}

	public HelpPage getHelpPage() {
		return help;
	}

	public List<Command<T>> getCommands() {
		return wrapped;
	}

	public Collection<String> getCompletions(String[] args) {
		// todo
		return null;
	}

	public boolean execute(String[] args) {
		// todo
		return false;
	}

}
