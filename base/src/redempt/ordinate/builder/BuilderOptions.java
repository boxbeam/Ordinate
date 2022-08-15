package redempt.ordinate.builder;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.processing.MiscUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents options for a {@link CommandBuilder}
 * @param <T> The sender type
 * @author Redempt
 */
public class BuilderOptions<T> {

	public static <T> BuilderOptions<T> getDefaults() {
		BuilderOptions<T> options = new BuilderOptions<>();
		options.addType(Integer.class, s -> Integer.parseInt(s));
		options.addType(Long.class, s -> Long.parseLong(s));
		options.addType(Double.class, Double::parseDouble);
		options.addType(Float.class, Float::parseFloat);
		options.addType(String.class, s -> s);
		options.addType(Boolean.class, MiscUtils::parseBoolean).completer(ctx -> Arrays.asList("true", "false"));
		return options;
	}
	
	private Map<Class<?>, ArgType<T, ?>> types = new HashMap<>();
	private String helpSubcommandName = "help";
	
	/**
	 * Sets the help subcommand name, specify null for no auto-generated help subcommands
	 * @param name The auto-generated help subcommand name
	 */
	public void setHelpSubcommandName(String name) {
		this.helpSubcommandName = name;
	}
	
	/**
	 * Registers a type for use in builders
	 * @param clazz The class of the type
	 * @param converter A function to parse the type
	 * @return The constructed {@link ArgType}, in case you want to apply a completer
	 * @param <V> The type
	 */
	public <V> ArgType<T, V> addType(Class<V> clazz, Function<String, V> converter) {
		ArgType<T, V> argType = new ArgType<>(clazz.getSimpleName().toLowerCase(), converter);
		types.put(clazz, argType);
		return argType;
	}
	
	/**
	 * Registers a type for use in builders
	 * @param clazz The class of the type
	 * @param converter A function to parse the type
	 * @return The constructed {@link ArgType}, in case you want to apply a completer
	 * @param <V> The type
	 */
	public <V> ArgType<T, V> addType(Class<V> clazz, BiFunction<CommandContext<T>, String, V> converter) {
		ArgType<T, V> argType = new ArgType<>(clazz.getSimpleName().toLowerCase(), converter);
		types.put(clazz, argType);
		return argType;
	}
	
	/**
	 * Gets the {@link ArgType} associated with a class
	 * @param clazz The class
	 * @return The argument type
	 * @throws IllegalArgumentException If no argument type for that class exists
	 */
	public ArgType<T, ?> getType(Class<?> clazz) {
		return getOrError(types, clazz, () -> "No argument type defined for " + clazz.getName());
	}
	
	/**
	 * @return The name to be used for auto-generated help subcommands
	 */
	public String getHelpSubcommandName() {
		return helpSubcommandName;
	}
	
	private <K, V> V getOrError(Map<K, V> map, K key, Supplier<String> error) {
		V value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException(error.get());
		}
		return value;
	}
	
}
