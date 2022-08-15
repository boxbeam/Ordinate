package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.*;

/**
 * Represents a collection of help entries, one for each command
 * @param <T> The sender type
 * @author Redempt
 */
public class HelpPage<T> {

	private Map<Command<T>, HelpEntry<T>> entries;

	public HelpPage(Map<Command<T>, HelpEntry<T>> entries) {
		this.entries = entries;
		entries.values().forEach(entry -> entry.page = this);
	}
	
	/**
	 * Gets the help entry for a given command
	 * @param command The command
	 * @return The help entry
	 */
	public HelpEntry<T> getHelp(Command<T> command) {
		return entries.get(command);
	}
	
	/**
	 * Gets the help entry for a given command and all of its subcommands recursively
	 * @param command The command
	 * @param filterDescriptionless Whether commands that have no description should be removed from the results
	 * @return The help entries
	 */
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
	
	/**
	 * @return All available help entries
	 */
	public Collection<HelpEntry<T>> getAll() {
		return entries.values();
	}

}
