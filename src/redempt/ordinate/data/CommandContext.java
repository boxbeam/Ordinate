package redempt.ordinate.data;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

public class CommandContext<T> {

	private CommandContext<T> parent;
	private T sender;
	private Queue<Argument> args = new ArrayDeque<>();
	private Object[] parsed;
	private int lastParsed = -1;
	
	public CommandContext(CommandContext<T> parent, T sender, Collection<Argument> args, int processAllocation) {
		this.parent = parent;
		this.sender = sender;
		this.args.addAll(args);
		parsed = new Object[processAllocation + 1];
		parsed[0] = sender;
	}
	
	public Argument peekArg() {
		return args.peek();
	}
	
	public Argument pollArg() {
		return args.poll();
	}
	
	public T sender() {
		return sender;
	}
	
	public int getLastParsed() {
		return lastParsed;
	}
	
	public void setParsed(int pos, Object parsed) {
		lastParsed = Math.max(lastParsed, pos);
		this.parsed[pos + 1] = parsed;
	}
	
	public Object getParsed(int pos) {
		return parsed[pos + 1];
	}
	
	public int getArgCount() {
		return parsed.length - 1;
	}
	
	public CommandContext<T> getParent() {
		return parent;
	}
	
	public Object lastParsed(int pos) {
		if (pos == 0 && parent != null && parent.parsed.length > 1) {
			return parent.parsed[parent.parsed.length - 1];
		}
		if (pos == 0) {
			return null;
		}
		return parsed[pos];
	}
	
}
