package redempt.ordinate.help;

import redempt.ordinate.component.CommandComponent;

import java.util.StringJoiner;

public class DelimitedHelpComponent implements HelpComponent {
	
	private CommandComponent<?> owner;
	private String delimiter;
	private HelpComponent[] components;
	private int priority;
	
	public DelimitedHelpComponent(CommandComponent<?> owner, int priority, String delimiter, HelpComponent... components) {
		this.owner = owner;
		this.priority = priority;
		this.delimiter = delimiter;
		this.components = components;
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
	
	public HelpComponent[] getChildren() {
		return components;
	}
	
}
