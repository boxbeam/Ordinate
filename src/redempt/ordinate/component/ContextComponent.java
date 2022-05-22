package redempt.ordinate.component;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;

import java.util.function.Function;

public class ContextComponent<T, V> extends CommandComponent<T> {
	
	private String name;
	private Function<CommandContext<T>, V> contextProvider;
	private String[] error;
	
	public ContextComponent(String name, Function<CommandContext<T>, V> contextProvider, String... error) {
		this.name = name;
		this.contextProvider = contextProvider;
		this.error = error;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public int getPriority() {
		return 10;
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
			return success().remove();
		}
		return failure(error);
	}
	
}
