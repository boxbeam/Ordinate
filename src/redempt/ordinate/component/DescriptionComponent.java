package redempt.ordinate.component;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.help.LiteralHelpComponent;

public class DescriptionComponent<T> extends CommandComponent<T> implements HelpProvider {

	private final String description;

	public DescriptionComponent(String description) {
		this.description = description;
	}

	@Override
	public int getMaxParsedObjects() {
		return 0;
	}

	@Override
	public int getMaxConsumedArgs() {
		return 0;
	}

	@Override
	public int getPriority() {
		return -10;
	}

	@Override
	public HelpComponent getHelpComponent() {
		return new LiteralHelpComponent(this, -10, false, description);
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		return success();
	}

}
