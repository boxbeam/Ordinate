package redempt.ordinate.help;

public interface HelpDisplayer<T> {
	
	public void display(T sender, HelpEntry<T> entry);

	public default void display(T sender, HelpEntry<T>... entries) {
		for (HelpEntry<T> entry : entries) {
			display(sender, entry);
		}
	}

}
