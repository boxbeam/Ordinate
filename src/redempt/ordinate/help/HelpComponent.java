package redempt.ordinate.help;

import redempt.ordinate.component.CommandComponent;

public interface HelpComponent {
	
	public int getPriority();
	public String getValue();
	public CommandComponent<?> getSource();
	
}
