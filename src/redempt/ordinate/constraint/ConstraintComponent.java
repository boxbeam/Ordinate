package redempt.ordinate.constraint;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.processing.MessageFormatter;

public class ConstraintComponent<T, V> extends CommandComponent<T> implements Named {

	private Constraint<T, V> constraint;
	private MessageFormatter<T> errorGenerator;
	private String name;

	public ConstraintComponent(Constraint<T, V> constraint, String name, MessageFormatter<T> errorGenerator) {
		this.constraint = constraint;
		this.errorGenerator = errorGenerator;
		this.name = name;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		V val = (V) context.getParsed(getIndex());
		String err = constraint.apply(context, val);
		if (err != null) {
			return failure(errorGenerator.apply(context.sender(), name, err)).complete();
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
