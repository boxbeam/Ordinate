package redempt.ordinate.context;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.Named;

import java.util.function.Function;

public interface ContextProvider<T, V> extends Named {

	public static <T, V> ContextProvider<T, V> create(String name, Function<CommandContext<T>, V> provider) {
		return new ContextProvider<T, V>() {
			@Override
			public V provide(CommandContext<T> context) {
				return provider.apply(context);
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}

	public V provide(CommandContext<T> context);

}
