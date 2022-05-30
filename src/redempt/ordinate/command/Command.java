package redempt.ordinate.command;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.Argument;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.Named;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.processing.CachedSupplier;
import redempt.ordinate.processing.CommandParsingPipeline;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class Command<T> extends CommandComponent<T> implements Named, HelpProvider {

	private Command<T> parent;
	private String mainName;
	private Set<String> names = new HashSet<>();
	private CommandParsingPipeline<T> pipeline;

	public Command(String[] names, CommandParsingPipeline<T> pipeline) {
		mainName = names[0];
		Collections.addAll(this.names, names);
		this.pipeline = pipeline;
	}

	public Command<T> getParent() {
		return parent;
	}

	public void setParent(Command<T> parent) {
		this.parent = parent;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public CommandParsingPipeline<T> getPipeline() {
		return pipeline;
	}

	public void preparePipeline() {
		pipeline.prepare();
		for (CommandComponent<T> component : pipeline.getComponents()) {
			component.setParent(this);
		}
	}

	@Override
	public int getMaxConsumedArgs() {
		return pipeline.getMaxArgWidth();
	}

	@Override
	public int getMaxParsedObjects() {
		return pipeline.getParsingSlots();
	}

	@Override
	public int getPriority() {
		return 20;
	}
	
	@Override
	public HelpComponent getHelpComponent() {
		return null;
	}
	
	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (isRoot()) {
			return pipeline.parse(context);
		}
		if (!context.hasArg()) {
			return success();
		}
		Argument arg = context.peekArg();
		if (arg.isQuoted() || !names.contains(arg.getValue())) {
			return success();
		}
		CommandContext<T> clone = context.clone(this, 1);
		CommandResult<T> result = pipeline.parse(clone);
		if (!result.isSuccess()) {
			result.uncomplete();
		}
		return result;
	}

	@Override
	public String getName() {
		return mainName;
	}

	public Set<String> getNames() {
		return names;
	}
	
}
