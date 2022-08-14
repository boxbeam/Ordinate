package redempt.ordinate.command.postarg;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;

public class ArgumentPropagationComponent<T> extends CommandComponent<T> {
	
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
	
}
