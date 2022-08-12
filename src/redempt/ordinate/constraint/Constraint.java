package redempt.ordinate.constraint;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.message.Message;

import java.util.List;

public interface Constraint<T, V> {

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

	public Message<T> apply(CommandContext<T> context, V value);

}
