package redempt.ordinate.message;

public interface Message<T> {
	
	public void send(T sender);
	
}
