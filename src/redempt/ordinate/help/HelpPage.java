package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.*;

public class HelpPage<T> {

	private Map<Command<T>, HelpEntry<T>> entries;

	public HelpPage(Map<Command<T>, HelpEntry<T>> entries) {
		this.entries = entries;
	}

	public HelpEntry<T> getHelp(Command<T> command) {
		return entries.get(command);
	}

	public HelpEntry<T>[] getHelpRecursive(Command<T> command, boolean filterDescriptionless) {
		List<HelpEntry<T>> entries = new ArrayList<>();
		Deque<Command<T>> queue = new ArrayDeque<>();
		queue.add(command);
		while (!queue.isEmpty()) {
			Command<T> cmd = queue.pollLast();
			HelpEntry<T> entry = getHelp(cmd);
			if (!filterDescriptionless || entry.getDescription() != null) {
				entries.add(entry);
			}
			queue.addAll(cmd.getSubcommands());
		}
		return entries.toArray(new HelpEntry[0]);
	}

	public Collection<HelpEntry<T>> getAll() {
		return entries.values();
	}

}
