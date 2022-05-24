package redempt.ordinate.constraint;

import redempt.ordinate.component.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.processing.Formatter;
import redempt.ordinate.help.HelpComponent;

public class ConstraintComponent<T, V> extends CommandComponent<T> {

	private Constraint<T, V> constraint;
	private Formatter errorGenerator;

	public ConstraintComponent(Constraint<T, V> constraint, Formatter errorGenerator) {
		this.constraint = constraint;
		this.errorGenerator = errorGenerator;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public HelpComponent getHelpDisplay() {
		return null;
	}

	@Override
	public boolean canParse(CommandContext<T> context) {
		return true;
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		V val = (V) context.getParsed(getIndex());
		String err = constraint.apply(context, val);
		if (err != null) {
			return failure(errorGenerator.apply(err));
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
