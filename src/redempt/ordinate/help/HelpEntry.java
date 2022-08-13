package redempt.ordinate.help;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.DescriptionComponent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class HelpEntry {

	private List<HelpComponent> components;
	private Command<?> owner;

	public HelpEntry(Command<?> owner, List<HelpComponent> components) {
		this.owner = owner;
		this.components = components;
	}

	public Command<?> getOwner() {
		return owner;
	}

	public List<HelpComponent> getComponents() {
		return components;
	}
	
	public String getOwnerPrefix() {
		Deque<String> out = new ArrayDeque<>();
		Command<?> parent = owner.getParent();
		while (parent != null) {
			out.addFirst(parent.getName());
			parent = parent.getParent();
		}
		return String.join(" ", out);
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
