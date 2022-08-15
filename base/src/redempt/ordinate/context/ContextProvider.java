package redempt.ordinate.context;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.Named;

import java.util.function.Function;

/**
 * Extracts values from a command's execution context so they can be passed as arguments to the final dispatcher
 * @param <T> The sender type
 * @param <V> The type being provided
 * @author Redempt
 */
public interface ContextProvider<T, V> extends Named {
	
	/**
	 * Creates a context provider
	 * @param name The name of the provider
	 * @param error The error to be shown if the provider returns null
	 * @param provider The function to provide the value
	 * @return A context provider
	 * @param <T> The sender type
	 * @param <V> The type being provided
	 */
	public static <T, V> ContextProvider<T, V> create(String name, String error, Function<CommandContext<T>, V> provider) {
		return new ContextProvider<T, V>() {
			@Override
			public V provide(CommandContext<T> context) {
				return provider.apply(context);
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getError() {
				return error;
			}
		};
	}
	
	/**
	 * Provides the context value for the given command execution context
	 * @param context The command execution context
	 * @return The generated context value
	 */
	public V provide(CommandContext<T> context);
	
	/**
	 * @return The error to be displayed if the context cannot be generated
	 */
	public String getError();

}
