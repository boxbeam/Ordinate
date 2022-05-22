package redempt.ordinate.help;

import redempt.ordinate.component.CommandComponent;

public class LiteralHelpComponent implements HelpComponent {

	private CommandComponent<?> owner;
	private String value;
	private int priority;
	
	public LiteralHelpComponent(CommandComponent<?> owner, int priority, String value) {
		this.owner = owner;
		this.value = value;
		this.priority = priority;
	}
	
	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public CommandComponent<?> getSource() {
		return owner;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
}
