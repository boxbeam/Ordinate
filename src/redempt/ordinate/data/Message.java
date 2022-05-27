package redempt.ordinate.data;

import redempt.ordinate.component.abstracts.CommandComponent;

public class Message<T, M, V> {
	
	private CommandComponent<T> component;
	private M message;
	private V value;
	
	public Message(CommandComponent<T> component, M message, V value) {
		this.component = component;
		this.message = message;
		this.value = value;
	}
	
	public CommandComponent<T> getComponent() {
		return component;
	}
	
	public M getMessage() {
		return message;
	}
	
	public V getValue() {
		return value;
	}
	
}
