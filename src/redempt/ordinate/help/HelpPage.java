package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.*;

public class HelpPage {

	private Map<Command<?>, HelpEntry> entries;

	public HelpPage(Map<Command<?>, HelpEntry> entries) {
		this.entries = entries;
	}

	public HelpEntry getHelp(Command<?> command) {
		return entries.get(command);
	}

	public List<HelpEntry> getHelpRecursive(Command<?> command) {
		List<HelpEntry> entries = new ArrayList<>();
		Deque<Command<?>> queue = new ArrayDeque<>();
		queue.add(command);
		while (!queue.isEmpty()) {
			Command<?> cmd = queue.pollLast();
			entries.add(getHelp(cmd));
			queue.addAll(cmd.getSubcommands());
		}
		return entries;
	}

	public Collection<HelpEntry> getAll() {
		return entries.values();
	}

}
