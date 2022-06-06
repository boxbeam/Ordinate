package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.List;
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

	@Override
	public String toString() {
		return components.stream().map(HelpComponent::getValue).collect(Collectors.joining(" "));
	}

}
