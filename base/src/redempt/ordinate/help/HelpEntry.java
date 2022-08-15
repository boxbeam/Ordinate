package redempt.ordinate.help;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.DescriptionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * Represents help metadata used to generate help messages for commands
 * @param <T> The sender type
 * @author Redempt
 */
public class HelpEntry<T> {

	private List<HelpComponent> components;
	private List<Predicate<T>> constraints = new ArrayList<>();
	private Command<T> owner;
	protected HelpPage<T> page;

	public HelpEntry(Command<T> owner, List<HelpComponent> components) {
		this.owner = owner;
		this.components = components;
	}
	
	/**
	 * @return The page this help entry is associated with
	 */
	public HelpPage<T> getPage() {
		return page;
	}
	
	/**
	 * @return The command this help entry describes
	 */
	public Command<T> getOwner() {
		return owner;
	}
	
	/**
	 * @return The individual components of this help entry
	 */
	public List<HelpComponent> getComponents() {
		return components;
	}
	
	/**
	 * Adds a filter to constrain the visibility of this help entry
	 * @param constraint The filter
	 */
	public void addFilter(Predicate<T> constraint) {
		constraints.add(constraint);
	}
	
	/**
	 * Checks this help entry's filters against the sender
	 * @param sender The sender
	 * @return Whether this help entry should be visible to the sender
	 */
	public boolean isVisibleTo(T sender) {
		return constraints.stream().allMatch(f -> f.test(sender));
	}
	
	/**
	 * @return The names of the parents of the command described by this help entry, joined by spaces
	 */
	public String getParentPrefix() {
		List<String> parentPrefix = new ArrayList<>();
		owner.getParentPrefix(getPage(), parentPrefix);
		parentPrefix.remove(parentPrefix.size() - 1);
		return String.join(" ", parentPrefix);
	}
	
	private boolean isDescription(HelpComponent c) {
		return c.getOwner() instanceof DescriptionComponent;
	}
	
	/**
	 * @return The name of the command followed by formatted arguments demonstrating its usage
	 */
	public String getUsage() {
		StringJoiner joiner = new StringJoiner(" ");
		components.stream().filter(c -> !isDescription(c)).forEach(c -> joiner.add(c.getValue()));
		return joiner.toString();
	}
	
	/**
	 * @return The help message for the command
	 */
	public String getDescription() {
		HelpComponent last = components.get(components.size() - 1);
		return isDescription(last) ? last.getValue() : null;
	}
	
	/**
	 * @return A simple but potentially incomplete description of the command
	 */
	@Override
	public String toString() {
		return getUsage() + ": " + getDescription();
	}

}
