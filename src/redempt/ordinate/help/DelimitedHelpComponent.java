package redempt.ordinate.help;

import redempt.ordinate.component.abstracts.CommandComponent;

import java.util.StringJoiner;

public class DelimitedHelpComponent implements HelpComponent {
	
	private CommandComponent<?> owner;
	private String delimiter;
	private HelpComponent[] components;
	private int priority;
	private boolean line;
	
	public DelimitedHelpComponent(CommandComponent<?> owner, int priority, boolean line, String delimiter, HelpComponent... components) {
		this.owner = owner;
		this.priority = priority;
		this.delimiter = delimiter;
		this.components = components;
		this.line = line;
	}
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public String getValue() {
		StringJoiner joiner = new StringJoiner(delimiter);
		for (HelpComponent component : components) {
			joiner.add(component.getValue());
		}
		return joiner.toString();
	}
	
	@Override
	public CommandComponent<?> getSource() {
		return owner;
	}

	@Override
	public boolean isLine() {
		return line;
	}

	public HelpComponent[] getChildren() {
		return components;
	}
	
}
