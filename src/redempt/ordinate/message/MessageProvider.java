package redempt.ordinate.message;

public interface MessageProvider<T> {

	public MessageFormatter<T> getFormatter(String name);
	
}
