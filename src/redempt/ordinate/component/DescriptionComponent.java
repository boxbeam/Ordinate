package redempt.ordinate.component;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;

import java.util.ArrayList;
import java.util.List;

public class DescriptionComponent<T> extends CommandComponent<T> implements HelpProvider {

	private final List<String> description = new ArrayList<>();

	public DescriptionComponent(String description) {
		this.description.add(description);
	}

	public void addLine(String line) {
		description.add(line);
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
		help.addHelp(new HelpComponent(this, -10, String.join("\n", description)));
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		return success();
	}

}
