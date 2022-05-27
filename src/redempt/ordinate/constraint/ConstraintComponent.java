package redempt.ordinate.constraint;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.processing.MessageFormatter;
import redempt.ordinate.help.HelpComponent;

public class ConstraintComponent<T, V> extends CommandComponent<T> {

	private Constraint<T, V> constraint;
	private MessageFormatter<T> errorGenerator;

	public ConstraintComponent(Constraint<T, V> constraint, MessageFormatter<T> errorGenerator) {
		this.constraint = constraint;
		this.errorGenerator = errorGenerator;
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
			return failure(errorGenerator.apply(context.sender(), err)).complete();
		}
		return success();
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
