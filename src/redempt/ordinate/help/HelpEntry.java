package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.List;

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

}
