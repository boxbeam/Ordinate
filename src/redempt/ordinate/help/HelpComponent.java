package redempt.ordinate.help;

import redempt.ordinate.component.abstracts.CommandComponent;

public class HelpComponent {

	private int priority;
	private String value;
	private CommandComponent<?> owner;

	public HelpComponent(CommandComponent<?> owner, int priority, String value) {
		this.priority = priority;
		this.value = value;
		this.owner = owner;
	}

	public int getPriority() {
		return priority;
	}

	public String getValue() {
		return value;
	}

	public CommandComponent<?> getOwner() {
		return owner;
	}
	
}
