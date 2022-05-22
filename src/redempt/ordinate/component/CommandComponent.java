package redempt.ordinate.component;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;

public abstract class CommandComponent<T> {
	
	private int index;
	private CommandComponent<T> parent;
	private int depth;
	
	protected void setParent(CommandComponent<T> parent) {
		this.parent = parent;
		depth = parent == null ? 0 : parent.depth + 1;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public CommandComponent<T> getParent() {
		return parent;
	}
	
	public CommandResult<T> success() {
		return new CommandResult<>(this, null);
	}
	
	public CommandResult<T> failure(String... error) {
		return new CommandResult<>(this, error);
	}
	
	public int getMaxWidth() {
		return 1;
	}
	
	public abstract int getPriority();
	public abstract boolean canParse(CommandContext<T> context);
	public abstract CommandResult<T> parse();
	
}
