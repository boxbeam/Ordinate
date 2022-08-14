package redempt.ordinate.builder;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.data.CommandContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class BuilderOptions<T> {

	private Map<Class<?>, ArgType<T, ?>> types = new HashMap<>();
	
	public <V> ArgType<T, V> addType(Class<V> clazz, Function<String, V> converter) {
		return new ArgType<>(clazz.getSimpleName().toLowerCase(), converter);
	}
	
	public <V> ArgType<T, V> addType(Class<V> clazz, BiFunction<CommandContext<T>, String, V> converter) {
		return new ArgType<>(clazz.getSimpleName().toLowerCase(), converter);
	}

	public ArgType<T, ?> getType(Class<?> clazz) {
		return getOrError(types, clazz, () -> "No argument type defined for " + clazz.getName());
	}
	
	private <K, V> V getOrError(Map<K, V> map, K key, Supplier<String> error) {
		V value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException(error.get());
		}
		return value;
	}
	
}
