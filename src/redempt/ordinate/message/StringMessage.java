package redempt.ordinate.message;

import java.util.function.BiConsumer;

public class StringMessage<T> implements Message<T> {
	
	private String message;
	private BiConsumer<T, String> send;
	
	public StringMessage(String message, BiConsumer<T, String> send) {
		this.message = message;
		this.send = send;
	}
	
	public String getMessage() {
		return message;
	}
	
	@Override
	public String toString() {
		return message;
	}
	
	@Override
	public void send(T sender) {
		send.accept(sender, message);
	}
	
}
