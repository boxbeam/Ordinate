package redempt.ordinate.data;

import redempt.ordinate.command.Command;

import java.util.HashMap;
import java.util.Map;

public class CommandContext<T> {

	private Map<Class<?>, SplittableStack<Object>> dependables = new HashMap<>();
	private Command<T> command;
	private CommandContext<T> parent;
	private T sender;
	private SplittableList<Argument> args;
	private int initialArgCount;
	private Object[] parsed;
	
	public CommandContext(Command<T> command, CommandContext<T> parent, T sender, SplittableList<Argument> args, int processAllocation) {
		this.command = command;
		this.parent = parent;
		this.sender = sender;
		this.args = args;
		initialArgCount = args.size();
		parsed = new Object[processAllocation + 1];
		parsed[0] = sender;
	}

	public void provide(Object dependable) {
		dependables.computeIfAbsent(dependable.getClass(), k -> new SplittableStack<>()).push(dependable);
	}

	public <V> void provide(Class<V> clazz, V dependable) {
		dependables.computeIfAbsent(clazz, k -> new SplittableStack<>()).push(dependable);
	}

	public <V> V request(Class<V> clazz, boolean remove) {
		SplittableStack<V> stack = (SplittableStack<V>) dependables.get(clazz);
		if (stack == null || stack.size() == 0) {
			throw new IllegalStateException("Unable to provide dependency value of type " + clazz.getName());
		}
		return remove ? stack.pop() : stack.peek();
	}
	
	public <V> V request(Class<V> clazz) {
		return request(clazz, true);
	}
	
	public boolean hasDependable(Class<?> clazz) {
		return dependables.containsKey(clazz);
	}

	public Command<T> getCommand() {
		return command;
	}
	
	public Argument peekArg() {
		return args.peek();
	}
	
	public Argument pollArg() {
		return args.poll();
	}

	public void removeArgs(int index, int toRemove, boolean removeInitial) {
		args.removeRange(index, toRemove);
		if (removeInitial) {
			initialArgCount -= toRemove;
		}
	}

	public void removeArg(int index, boolean removeInitial) {
		args.remove(index);
		if (removeInitial) {
			initialArgCount--;
		}
	}

	public int initialArgCount() {
		return initialArgCount;
	}

	public boolean hasArg() {
		return args.hasNext();
	}
	
	public SplittableList<Argument> getArguments() {
		return args;
	}
	
	public T sender() {
		return sender;
	}
	
	public void setParsed(int pos, Object parsed) {
		this.parsed[pos + 1] = parsed;
	}
	
	public Object getParsed(int pos) {
		return parsed[pos + 1];
	}

	public Object[] getAllParsed() {
		return parsed;
	}

	public int getTotalParsingSlots() {
		return parsed.length - 1;
	}
	
	public CommandContext<T> getParent() {
		return parent;
	}
	
	public CommandContext<T> setParent(CommandContext<T> parent) {
		this.parent = parent;
		return this;
	}
	
	public CommandContext<T> clone(Command<T> command, int argsSplit, int parsingSlots) {
		CommandContext<T> clone = new CommandContext<>(command, parent, sender, args.split(argsSplit), parsingSlots);
		clone.parent = this;
		dependables.forEach((k, v) -> clone.dependables.put(k, v.split()));
		return clone;
	}
	
}
