package redempt.ordinate.dispatch;

public interface MessageDispatcher<T> {

	public void sendMessage(T sender, String message);

	public default void sendMessage(T sender, String... message) {
		for (String line : message) {
			sendMessage(sender, line);
		}
	}

}
