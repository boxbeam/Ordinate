package redempt.ordinate.command;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.*;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.processing.ArgumentSplitter;
import redempt.ordinate.processing.CommandParsingPipeline;

import java.util.*;

public class Command<T> extends CommandComponent<T> implements Named, HelpProvider {

	private String mainName;
	private Set<String> names = new HashSet<>();
	private CommandParsingPipeline<T> pipeline;
	private int priority = 20;

	public Command(String commandPrefix, String[] names, CommandParsingPipeline<T> pipeline) {
		mainName = names[0];
		Collections.addAll(this.names, names);
		this.pipeline = pipeline;
	}

	public CommandContext<T> createContext(T sender, String[] input, boolean forCompletions) {
		SplittableList<Argument> args = ArgumentSplitter.split(input, forCompletions);
		return new CommandContext<>(this, null, sender, args, pipeline.getParsingSlots());
	}

	public CommandContext<T> createContext(T sender, String input, boolean forCompletions) {
		SplittableList<Argument> args = ArgumentSplitter.split(input, forCompletions);
		return new CommandContext<>(this, null, sender, args, pipeline.getParsingSlots());
	}

	public boolean isRoot() {
		return getParent() == null;
	}

	public CommandParsingPipeline<T> getPipeline() {
		return pipeline;
	}

	public void preparePipeline() {
		pipeline.prepare();
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
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	@Override
	public HelpComponent getHelpComponent() {
		return new HelpComponent(this, 5, getName());
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
		CommandContext<T> clone = context.clone(this, 1, pipeline.getParsingSlots());
		CommandResult<T> result = pipeline.parse(clone);
		if (!result.isSuccess()) {
			result.uncomplete();
		}
		return result;
	}

	@Override
	public CommandResult<T> complete(CommandContext<T> context, Set<String> completions) {
		if (isRoot()) {
			completions.addAll(pipeline.completions(context));
			return success();
		}
		if (context.getArguments().size() == 1) {
			completions.addAll(names);
			context.pollArg();
			return success();
		}
		if (!context.hasArg()) {
			return success();
		}
		Argument arg = context.peekArg();
		if (arg.isQuoted() || !names.contains(arg.getValue())) {
			return success();
		}
 		context = context.clone(this, 1, pipeline.getParsingSlots());
		completions.addAll(pipeline.completions(context));
		return success();
	}

	@Override
	public String getName() {
		return mainName;
	}

	public Set<String> getNames() {
		return names;
	}
	
}
