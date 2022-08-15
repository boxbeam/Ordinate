package redempt.ordinate.constraint;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.message.Message;

import java.util.List;

/**
 * A predicate for a specific type that can return error messages
 * @param <T> The sender type
 * @param <V> The type the constraint is applied to
 */
public interface Constraint<T, V> {
	
	/**
	 * Converts a constraint to one which can be applied to a list
	 * @param constraint The constraint
	 * @return The converted constraint which can constrain a list
	 * @param <T> The sender type
	 * @param <V> The original type being constrained
	 */
	public static <T, V> Constraint<T, List<V>> listConstraint(Constraint<T, V> constraint) {
		return (ctx, arr) -> {
			for (V val : arr) {
				Message<T> err = constraint.apply(ctx, val);
				if (err != null) {
					return err;
				}
			}
			return null;
		};
	}
	
	/**
	 * Checks whether a value is valid
	 * @param context The context of command execution
	 * @param value The value to apply the constraint to
	 * @return An error if the constraint check failed, otherwise null
	 */
	public Message<T> apply(CommandContext<T> context, V value);

}
