package redempt.ordinate.component;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;

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
	public void addHelp(HelpBuilder help) {
		help.addHelp(new HelpComponent(this, -10, description));
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		return success();
	}

}
