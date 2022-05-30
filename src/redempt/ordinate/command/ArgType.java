package redempt.ordinate.command;

import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.Named;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public ArgType(String name, BiFunction<CommandContext<T>, String, V> converter) {
		this(name, converter, (ctx, str) -> Collections.emptyList());
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

	public ArgType<T, V> completer(BiFunction<CommandContext<T>, String, List<String>> completer) {
		this.completer = completer;
		return this;
	}

	public ArgType<T, V> completerStream(BiFunction<CommandContext<T>, String, Stream<String>> completer) {
		this.completer = (ctx, str) -> completer.apply(ctx, str).collect(Collectors.toList());
		return this;
	}

	public ConstraintParser<T, V> getConstraintParser() {
		return constraintParser;
	}
	
}
