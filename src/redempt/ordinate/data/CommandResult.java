package redempt.ordinate.data;

import redempt.ordinate.component.CommandComponent;

public class CommandResult<T> {
	
	public static <T> CommandResult<T> deeper(CommandResult<T> first, CommandResult<T> second) {
		return first.component.getDepth() >= second.component.getDepth() ? first : second;
	}
	
	private CommandComponent<T> component;
	private String[] error;
	private boolean complete = false;
	
	public CommandResult(CommandComponent<T> component, String[] error) {
		this.component = component;
		this.error = error;
	}
	
	public CommandComponent<T> getComponent() {
		return component;
	}

	public CommandResult<T> complete() {
		complete = true;
		return this;
	}

	public boolean isComplete() {
		return complete;
	}

	public boolean isSuccess() {
		return error == null;
	}
	
	public String[] getError() {
		return error;
	}
	
}
