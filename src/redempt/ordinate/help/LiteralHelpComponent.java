package redempt.ordinate.help;

import redempt.ordinate.component.abstracts.CommandComponent;

public class LiteralHelpComponent implements HelpComponent {

	private CommandComponent<?> owner;
	private String value;
	private int priority;
	private boolean line;
	
	public LiteralHelpComponent(CommandComponent<?> owner, int priority, boolean line, String value) {
		this.owner = owner;
		this.value = value;
		this.priority = priority;
		this.line = line;
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
	public boolean isLine() {
		return line;
	}

	@Override
	public int getPriority() {
		return priority;
	}
	
}
