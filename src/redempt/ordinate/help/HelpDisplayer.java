package redempt.ordinate.help;

public interface HelpDisplayer<T> {

	public void display(T sender, HelpEntry entry);

	public default void display(T sender, HelpEntry... entries) {
		for (HelpEntry entry : entries) {
			display(sender, entry);
		}
	}

}
