package redempt.ordinate.parser;

import redempt.ordinate.component.argument.ArgType;
import redempt.ordinate.context.ContextProvider;

import java.util.Map;
import java.util.function.Supplier;

public class ParserConfig<T> {

	private ArgumentParser<T> argumentParser;
	private Map<String, TagProcessor<T>> tagProcessors;
	private Map<String, ArgType<T, ?>> argumentTypes;
	private Map<String, ContextProvider<T, ?>> contextProviders;

	public ArgumentParser<T> getArgumentParser() {
		return argumentParser;
	}

	private <V> V getOrError(Map<String, V> map, String key, Supplier<String> error) {
		V value = map.get(key);
		if (value == null) {
			throw new IllegalArgumentException(error.get());
		}
		return value;
	}

	public TagProcessor<T> getTagProcessor(String name) {
		return getOrError(tagProcessors, name, () -> "Missing TagProcessor for tag " + name);
	}

	public ArgType<T, ?> getArgType(String name) {
		return getOrError(argumentTypes, name, () -> "Missing ArgType with name " + name);
	}

	public ContextProvider<T, ?> getContextProvider(String name) {
		return getOrError(contextProviders, name, () -> "Missing ContextProvider with name " + name);
	}

}
