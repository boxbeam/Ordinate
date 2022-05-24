package redempt.ordinate.command;

import redempt.ordinate.component.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.processing.CommandParsingPipeline;

public class Command<T> extends CommandComponent<T> implements Named {
	
	private String[] names;
	private CommandParsingPipeline<T> pipeline;

	public CommandParsingPipeline<T> getPipeline() {
		return pipeline;
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
		return 0;
	}
	
	@Override
	public HelpComponent getHelpDisplay() {
		return null;
	}
	
	@Override
	public boolean canParse(CommandContext<T> context) {
		return false;
	}
	
	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		return null;
	}
	
	@Override
	public String getName() {
		return names[0];
	}
	
}
