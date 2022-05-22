package redempt.ordinate.data;

public class HelpComponent {

	private String value;
	private int priority;
	
	public HelpComponent(String value, int priority) {
		this.value = value;
		this.priority = priority;
	}
	
	public String getValue() {
		return value;
	}
	
	public int getPriority() {
		return priority;
	}
	
}
