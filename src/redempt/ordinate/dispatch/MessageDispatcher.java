package redempt.ordinate.dispatch;

public interface MessageDispatcher<T> {

	public void sendMessage(T sender, String message);

}
