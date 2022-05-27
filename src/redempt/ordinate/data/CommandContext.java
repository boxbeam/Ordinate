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
	private Object[] parsed;
	
	public CommandContext(Command<T> command, CommandContext<T> parent, T sender, SplittableList<Argument> args, int processAllocation) {
		this.command = command;
		this.parent = parent;
		this.sender = sender;
		this.args = args;
		parsed = new Object[processAllocation + 1];
		parsed[0] = sender;
	}

	public void provide(Object dependable) {
		dependables.computeIfAbsent(dependable.getClass(), k -> new SplittableStack<>()).push(dependable);
	}

	public <V> void provide(Class<V> clazz, V dependable) {
		dependables.computeIfAbsent(clazz, k -> new SplittableStack<>()).push(dependable);
	}

	public <V> V request(Class<V> clazz) {
		SplittableStack<V> stack = (SplittableStack<V>) dependables.get(clazz);
		if (stack == null || stack.size() == 0) {
			throw new IllegalStateException("Unable to provide dependency value of type " + clazz.getName());
		}
		return stack.pop();
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

	public Argument lastArg() {
		return args.size() > 0 ? args.get(args.size() - 1) : null;
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
	
	public int getArgCount() {
		return parsed.length - 1;
	}
	
	public CommandContext<T> getParent() {
		return parent;
	}
	
	public CommandContext<T> clone(Command<T> command) {
		CommandContext<T> clone = new CommandContext<>(command, parent, sender, args.split(0), parsed.length - 1);
		dependables.forEach((k, v) -> clone.dependables.put(k, v.split()));
		return clone;
	}
	
}
