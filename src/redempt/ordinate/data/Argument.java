package redempt.ordinate.data;

public class Argument {
	
	private String value;
	private boolean quoted;
	
	public Argument(String value, boolean quoted) {
		this.value = value;
		this.quoted = quoted;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isQuoted() {
		return quoted;
	}
	
}
