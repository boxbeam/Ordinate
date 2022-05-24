package redempt.ordinate.argument;

import redempt.ordinate.component.CommandComponent;
import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.Message;
import redempt.ordinate.data.Named;

import java.util.List;
import java.util.function.BiFunction;

public class ArgType<T, V> implements Named {

	private String name;
	private String errorMessage;
	private BiFunction<CommandContext<T>, Integer, V> converter;
	private BiFunction<CommandContext<T>, Integer, List<String>> completer;
	private ConstraintParser<T, V> constraintParser;

	public ArgType(String name, String errorMessage, BiFunction<CommandContext<T>, Integer, V> converter, BiFunction<CommandContext<T>, Integer, List<String>> completer) {
		this.name = name;
		this.errorMessage = errorMessage;
		this.converter = converter;
		this.completer = completer;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Message<T, String, V> convert(CommandComponent<T> component, CommandContext<T> context, int index) {
		try {
			V val = converter.apply(context, index);
			if (val == null) {
				return new Message<>(component, errorMessage, null);
			}
			return new Message<>(component, null, val);
		} catch (Exception e) {
			return new Message<>(component, errorMessage, null);
		}
	}
	
	public List<String> complete(CommandContext<T> context, int index) {
		return completer.apply(context, index);
	}

	public ArgType<T, V> constraint(ConstraintParser<T, V> constraintParser) {
		this.constraintParser = constraintParser;
		return this;
	}

	public ConstraintParser<T, V> getConstraintParser() {
		return constraintParser;
	}
	
}
