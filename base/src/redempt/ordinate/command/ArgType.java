package redempt.ordinate.command;

import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.Named;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a parsable type that can be used in command arguments
 * @param <T> The sender type
 * @param <V> The argument type
 * @author Redempt
 */
public class ArgType<T, V> implements Named {

	public static <T, V> ArgType<T, V> of(String name, Map<String, V> map) {
		return new ArgType<T, V>(name, (ctx, val) -> map.get(val)).completer((ctx, val) -> map.keySet());
	}
	
	private String name;
	private BiFunction<CommandContext<T>, String, V> converter;
	private BiFunction<CommandContext<T>, String, Collection<String>> completer = (ctx, s) -> Collections.emptyList();
	private ConstraintParser<T, V> constraintParser;
	
	/**
	 * @param name The name of the ArgType
	 * @param converter A function to parse the type
	 */
	public ArgType(String name, BiFunction<CommandContext<T>, String, V> converter) {
		this.name = name;
		this.converter = converter;
	}
	
	/**
	 * @param name The name of the ArgType
	 * @param converter A function to parse the type
	 */
	public ArgType(String name, Function<String, V> converter) {
		this(name, (ctx, val) -> converter.apply(val));
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * Convert an argument
	 * @param context The context of the command's execution
	 * @param arg The string value of the argument
	 * @return The parsed value
	 */
	public V convert(CommandContext<T> context, String arg) {
		try {
			return converter.apply(context, arg);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Get completions for a given partial string
	 * @param context The context of the command's execution
	 * @param partial The partial argument
	 * @return A collection of completions
	 */
	public Collection<String> complete(CommandContext<T> context, String partial) {
		return completer.apply(context, partial);
	}
	
	/**
	 * Assign a {@link ConstraintParser}, which can be used to set constraints for this type in the command file
	 * @param constraintParser The constraint parser
	 * @return Itself
	 */
	public ArgType<T, V> constraint(ConstraintParser<T, V> constraintParser) {
		this.constraintParser = constraintParser;
		return this;
	}
	
	/**
	 * Set how completions for this type should be computed
	 * @param completer A function which provides completions
	 * @return Itself
	 */
	public ArgType<T, V> completer(BiFunction<CommandContext<T>, String, Collection<String>> completer) {
		this.completer = completer;
		return this;
	}
	
	/**
	 * Set how completions for this type should be computed
	 * @param completer A function which provides completions
	 * @return Itself
	 */
	public ArgType<T, V> completer(Function<CommandContext<T>, Collection<String>> completer) {
		this.completer = (c, s) -> completer.apply(c);
		return this;
	}
	
	/**
	 * Set how completions for this type should be computed
	 * @param completer A function which provides completions
	 * @return Itself
	 */
	public ArgType<T, V> completerStream(BiFunction<CommandContext<T>, String, Stream<String>> completer) {
		this.completer = (ctx, str) -> completer.apply(ctx, str).collect(Collectors.toList());
		return this;
	}
	
	/**
	 * Set how completions for this type should be computed
	 * @param completer A function which provides completions
	 * @return Itself
	 */
	public ArgType<T, V> completerStream(Function<CommandContext<T>, Stream<String>> completer) {
		this.completer = (ctx, str) -> completer.apply(ctx).collect(Collectors.toList());
		return this;
	}
	
	/**
	 * @return The constraint parser for this type
	 */
	public ConstraintParser<T, V> getConstraintParser() {
		return constraintParser;
	}
	
}
