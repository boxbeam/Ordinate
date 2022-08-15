package redempt.ordinate.builder;

import java.util.Map;

/**
 * Represents the parsed arguments for a single command execution
 * @param <T> The sender type
 * @author Redempt
 */
public class CommandArguments<T> {
	
	private T sender;
	private Object[] args;
	private Map<String, Integer> indexMap;
	
	public CommandArguments(T sender, Object[] args, Map<String, Integer> indexMap) {
		this.sender = sender;
		this.args = args;
		this.indexMap = indexMap;
	}
	
	/**
	 * @return The sender of the command
	 */
	public T sender() {
		return sender;
	}
	
	/**
	 * Gets an argument by name
	 * @param argName The name of the argument
	 * @return The value of the argument
	 * @param <V> The argument's type
	 * @throws IllegalArgumentException If the given argument name is not found
	 */
	public <V> V get(String argName) {
		Integer index = indexMap.get(argName);
		if (index == null) {
			throw new IllegalArgumentException("Unknown argument " + argName);
		}
		return get(index);
	}
	
	/**
	 * Gets an argument by index. Recommended to use {@link CommandArguments#get(String)} for clarity.
	 * @param index The index of the argument
	 */
	public <V> V get(int index) {
		return (V) args[index + 1];
	}

}
