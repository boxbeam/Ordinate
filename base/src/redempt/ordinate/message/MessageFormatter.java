package redempt.ordinate.message;

public interface MessageFormatter<T> {

	public Message<T> format(T sender, String... context);
	
}
