package redempt.ordinate.command;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.CommandParent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.*;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.processing.CommandPipeline;

import java.util.*;

public class Command<T> extends CommandComponent<T> implements Named, HelpProvider {

	private String mainName;
	private Set<String> names = new HashSet<>();
	private CommandPipeline<T> pipeline;
	private int priority = 20;
	private boolean lookup = true;

	public Command(String[] names, CommandPipeline<T> pipeline) {
		mainName = names[0];
		Collections.addAll(this.names, names);
		this.pipeline = pipeline;
	}

	public CommandContext<T> createContext(T sender, SplittableList<Argument> args) {
		return new CommandContext<>(this, null, sender, args, pipeline.getParsingSlots());
	}

	public boolean canLookup() {
		return lookup;
	}

	public void setLookup(boolean lookup) {
		this.lookup = lookup;
	}

	public boolean isRoot() {
		return getParent() == null;
	}

	public CommandPipeline<T> getPipeline() {
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

	public HelpComponent getHelpComponent() {
		return new HelpComponent(this, 5, getName());
	}

	@Override
	public void addHelp(HelpBuilder help) {
		help.addHelp(getHelpComponent());
		for (CommandComponent<T> component : pipeline.getComponents()) {
			if (component instanceof HelpProvider) {
				((HelpProvider) component).addHelp(help);
			}
		}
	}

	@Override
	public CommandResult<T> parse(CommandContext<T> context) {
		if (isRoot()) {
			return pipeline.parse(context, this::failure);
		}
		if (!context.hasArg()) {
			return success();
		}
		Argument arg = context.peekArg();
		if (arg.isQuoted() || !names.contains(arg.getValue())) {
			return success();
		}
		CommandContext<T> clone = context.clone(this, 1, pipeline.getParsingSlots());
		CommandResult<T> result = pipeline.parse(clone, this::failure);
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
		if (!context.hasArg()) {
			completions.addAll(names);
			context.pollArg();
			return success();
		}
		Argument arg = context.peekArg();
		if (arg.isQuoted() || !names.contains(arg.getValue())) {
			return success();
		}
 		context = context.clone(this, 1, pipeline.getParsingSlots());
		completions.addAll(pipeline.completions(context));
		return failure();
	}

	@Override
	public String getName() {
		return mainName;
	}

	public Set<String> getNames() {
		return names;
	}
	
	public void getParentPrefix(List<String> parts) {
		if (getParent() != null) {
			getParent().getParentPrefix(parts);
		}
		parts.add(getName());
	}

	public List<Command<T>> getSubcommands() {
		List<Command<T>> subcommands = new ArrayList<>();
		for (CommandComponent<T> component : pipeline.getComponents()) {
			if (component instanceof Command) {
				subcommands.add((Command<T>) component);
				continue;
			}
			if (component instanceof CommandParent) {
				CommandParent<T> parent = (CommandParent<T>) component;
				subcommands.addAll(parent.getSubcommands());
			}
		}
		return subcommands;
	}
}
