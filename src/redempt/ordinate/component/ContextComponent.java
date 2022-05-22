package redempt.ordinate.component;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.help.HelpComponent;

import java.util.function.Function;

public class ContextComponent<T, V> extends CommandComponent<T> implements Named {
	
	private String name;
	private Function<CommandContext<T>, V> contextProvider;
	private String[] error;
	
	public ContextComponent(String name, Function<CommandContext<T>, V> contextProvider, String... error) {
		this.name = name;
		this.contextProvider = contextProvider;
		this.error = error;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getPriority() {
		return 10;
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
		V value = contextProvider.apply(context);
		if (value != null) {
			context.setParsed(getIndex(), value);
			return success();
		}
		return failure(error);
	}
	
}
