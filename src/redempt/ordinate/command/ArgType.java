package redempt.ordinate.command;

import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.Named;

import java.util.List;
import java.util.function.BiFunction;

public class ArgType<T, V> implements Named {

	private String name;
	private BiFunction<CommandContext<T>, String, V> converter;
	private BiFunction<CommandContext<T>, String, List<String>> completer;
	private ConstraintParser<T, V> constraintParser;

	public ArgType(String name, BiFunction<CommandContext<T>, String, V> converter, BiFunction<CommandContext<T>, String, List<String>> completer) {
		this.name = name;
		this.converter = converter;
		this.completer = completer;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public V convert(CommandContext<T> context, String arg) {
		try {
			return converter.apply(context, arg);
		} catch (Exception e) {
			return null;
		}
	}
	
	public List<String> complete(CommandContext<T> context, String partial) {
		return completer.apply(context, partial);
	}

	public ArgType<T, V> constraint(ConstraintParser<T, V> constraintParser) {
		this.constraintParser = constraintParser;
		return this;
	}

	public ConstraintParser<T, V> getConstraintParser() {
		return constraintParser;
	}
	
}
