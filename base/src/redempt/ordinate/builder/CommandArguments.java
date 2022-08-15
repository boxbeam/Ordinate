package redempt.ordinate.builder;

import java.util.Map;

public class CommandArguments<T> {
	
	private T sender;
	private Object[] args;
	private Map<String, Integer> indexMap;
	
	public CommandArguments(T sender, Object[] args, Map<String, Integer> indexMap) {
		this.sender = sender;
		this.args = args;
		this.indexMap = indexMap;
	}
	
	public T sender() {
		return sender;
	}
	
	public <V> V get(String argName) {
		Integer index = indexMap.get(argName);
		if (index == null) {
			throw new IllegalArgumentException("Unknown argument " + argName);
		}
		return get(index);
	}
	
	public <V> V get(int index) {
		return (V) args[index + 1];
	}

}
