package redempt.ordinate.data;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.message.Message;

public class CommandResult<T> {
	
	public static <T> CommandResult<T> deepest(CommandResult<T> first, CommandResult<T> second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		return first.component.getDepth() >= second.component.getDepth() ? first : second;
	}
	
	private CommandComponent<T> component;
	private Message<T> error;
	private boolean complete = false;
	
	public CommandResult(CommandComponent<T> component, Message<T> error) {
		this.component = component;
		this.error = error;
	}
	
	public CommandComponent<T> getComponent() {
		return component;
	}

	public Command<T> getCommand() {
		if (component instanceof Command) {
			return (Command<T>) component;
		}
		return component.getParent();
	}

	public CommandResult<T> complete() {
		complete = true;
		return this;
	}

	public CommandResult<T> uncomplete() {
		complete = false;
		return this;
	}

	public boolean isComplete() {
		return complete;
	}

	public boolean isSuccess() {
		return error == null;
	}
	
	public Message<T> getError() {
		return error;
	}

	@Override
	public String toString() {
		return (complete ? "Complete " : "Incomplete ") + (isSuccess() ? "Success" : "Error: " + error);
	}
	
}
