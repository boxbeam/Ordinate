package redempt.ordinate.constraint;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.message.Message;
import redempt.ordinate.message.MessageFormatter;

import java.util.function.Supplier;

public class ConstraintComponent<T, V> extends CommandComponent<T> implements Named {

	private Constraint<T, V> constraint;
	private MessageFormatter<T> errorGenerator;
	private String name;
	private Supplier<Integer> index;

	public ConstraintComponent(Constraint<T, V> constraint, Supplier<Integer> index, String name, MessageFormatter<T> errorGenerator) {
		this.constraint = constraint;
		this.errorGenerator = errorGenerator;
		this.name = name;
		this.index = index;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		V val = (V) context.getParsed(index.get());
		Message<T> err = constraint.apply(context, val);
		if (err != null) {
			return failure(errorGenerator.format(context.sender(), name, err.toString())).complete();
		}
		return success();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMaxParsedObjects() {
		return 0;
	}

	@Override
	public int getMaxConsumedArgs() {
		return 0;
	}
}
