package redempt.ordinate.component.abstracts;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpComponent;

import java.util.Set;

public abstract class CommandComponent<T> {
	
	private int index;
	private CommandComponent<T> parent;
	private int depth;
	
	public void setParent(CommandComponent<T> parent) {
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
	
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		return parse(context);
	}

	public int getMinConsumedArgs() {
		return getMaxConsumedArgs();
	}

	public abstract int getMaxConsumedArgs();
	public abstract int getMaxParsedObjects();
	public abstract int getPriority();
	public abstract CommandResult<T> parse(CommandContext<T> context);
	
}
