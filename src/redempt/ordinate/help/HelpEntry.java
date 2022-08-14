package redempt.ordinate.help;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.DescriptionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

public class HelpEntry<T> {

	private List<HelpComponent> components;
	private List<Predicate<T>> constraints = new ArrayList<>();
	private Command<T> owner;

	public HelpEntry(Command<T> owner, List<HelpComponent> components) {
		this.owner = owner;
		this.components = components;
	}

	public Command<T> getOwner() {
		return owner;
	}

	public List<HelpComponent> getComponents() {
		return components;
	}
	
	public void addFilter(Predicate<T> constraint) {
		constraints.add(constraint);
	}
	
	public boolean isVisibleTo(T sender) {
		return constraints.stream().allMatch(f -> f.test(sender));
	}
	
	public String getParentPrefix() {
		List<String> parentPrefix = new ArrayList<>();
		owner.getParentPrefix(parentPrefix);
		parentPrefix.remove(parentPrefix.size() - 1);
		return String.join(" ", parentPrefix);
	}
	
	private boolean isDescription(HelpComponent c) {
		return c.getOwner() instanceof DescriptionComponent;
	}
	
	public String getUsage() {
		StringJoiner joiner = new StringJoiner(" ");
		components.stream().filter(c -> !isDescription(c)).forEach(c -> joiner.add(c.getValue()));
		return joiner.toString();
	}
	
	public String getDescription() {
		HelpComponent last = components.get(components.size() - 1);
		return isDescription(last) ? last.getValue() : null;
	}
	
	@Override
	public String toString() {
		return getUsage() + ": " + getDescription();
	}

}
