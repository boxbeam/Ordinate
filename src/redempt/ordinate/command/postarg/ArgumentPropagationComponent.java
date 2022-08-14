package redempt.ordinate.command.postarg;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.help.HelpEntry;

public class ArgumentPropagationComponent<T> extends CommandComponent<T> implements HelpProvider<T> {
	
	private int pull;
	
	public void setPull(int pull) {
		this.pull = pull;
	}
	
	public int getPull() {
		return pull;
	}
	
	@Override
	public int getMaxConsumedArgs() {
		return 0;
	}
	
	@Override
	public int getMaxParsedObjects() {
		return pull;
	}
	
	@Override
	public int getPriority() {
		return -25;
	}
	
	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		Object[] parsed = context.getAllParsed();
		Object[] parent = context.getParent().getAllParsed();
		for (int i = parsed.length - 1; i > pull; i--) {
			parsed[i] = parsed[i - pull];
		}
		System.arraycopy(parent, 1, parsed, 1, pull);
		return success();
	}
	
	@Override
	public void addHelp(HelpBuilder<T> help) {
		HelpEntry<T> parentEntry = help.getPartialEntry(getParent().getParent());
		String usage = parentEntry.getUsage();
		String[] split = usage.split(" ", 2);
		help.addHelp(new HelpComponent(this, 50, split[1]));
	}
	
}
