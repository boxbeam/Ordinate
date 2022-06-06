package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.Map;

public class HelpPage {

	private Map<Command<?>, HelpEntry> entries;

	public HelpPage(Map<Command<?>, HelpEntry> entries) {
		this.entries = entries;
	}

	public HelpEntry getHelp(Command<?> command) {
		return entries.get(command);
	}

}
