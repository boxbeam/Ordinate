package redempt.ordinate.command;

import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.Named;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArgType<T, V> implements Named {

	public static <T, V> ArgType<T, V> of(String name, Map<String, V> map) {
		return new ArgType<T, V>(name, (ctx, val) -> map.get(val)).completer((ctx, val) -> map.keySet());
	}
	
	private String name;
	private BiFunction<CommandContext<T>, String, V> converter;
	private BiFunction<CommandContext<T>, String, Collection<String>> completer;
	private ConstraintParser<T, V> constraintParser;

	public ArgType(String name, BiFunction<CommandContext<T>, String, V> converter) {
		this.name = name;
		this.converter = converter;
	}
	
	public ArgType(String name, Function<String, V> converter) {
		this(name, (ctx, val) -> converter.apply(val));
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

	public Collection<String> complete(CommandContext<T> context, String partial) {
		return completer.apply(context, partial);
	}

	public ArgType<T, V> constraint(ConstraintParser<T, V> constraintParser) {
		this.constraintParser = constraintParser;
		return this;
	}

	public ArgType<T, V> completer(BiFunction<CommandContext<T>, String, Collection<String>> completer) {
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
