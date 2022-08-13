package redempt.ordinate.component;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;

public class HelpSubcommandComponent<T> extends CommandComponent<T> {
	
	private String name;
	
	public HelpSubcommandComponent(String name) {
		this.name = name;
	}
	
	@Override
	public int getMaxConsumedArgs() {
		return 0;
	}
	
	@Override
	public int getMaxParsedObjects() {
		return 0;
	}
	
	@Override
	public int getPriority() {
		return 25;
	}
	
	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (!context.hasArg() || context.peekArg().isQuoted()) {
			return success();
		}
		if (context.peekArg().getValue().equals(name)) {
			return failure().complete();
		}
		return success();
	}
	
}
