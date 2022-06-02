package redempt.ordinate.component;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.*;
import redempt.ordinate.help.HelpComponent;

import java.util.Set;

public class BooleanFlagComponent<T> extends CommandComponent<T> implements Named, HelpProvider {

	private final Set<String> names;
	private final String mainName;

	public BooleanFlagComponent(String mainName, Set<String> names) {
		this.mainName = mainName;
		this.names = names;
	}

	@Override
	public int getMaxConsumedArgs() {
		return 1;
	}

	@Override
	public int getMinConsumedArgs() {
		return 0;
	}

	@Override
	public String getName() {
		return mainName;
	}

	@Override
	public int getMaxParsedObjects() {
		return 1;
	}

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public HelpComponent getHelpComponent() {
		return new HelpComponent(this, 5, '[' + mainName + ']');
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		SplittableList<Argument> arguments = context.getArguments();
		boolean parsed = false;

		for (int i = 0; i < arguments.size(); i++) {
			Argument arg = arguments.get(i);
			String value = arg.getValue();

			if (arg.isQuoted()) {
				continue;
			}

			if (names.contains(value)) {
				parsed = true;
				context.removeArg(i, true);
				break;
			}
		}
		context.setParsed(getIndex(), parsed);
		return success();
	}

}
