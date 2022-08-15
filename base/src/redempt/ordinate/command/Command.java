package redempt.ordinate.command;

import redempt.ordinate.component.SubcommandLookupComponent;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.CommandParent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.*;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.dispatch.DispatchComponent;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.help.HelpPage;
import redempt.ordinate.processing.CommandPipeline;

import java.util.*;

public class Command<T> extends CommandComponent<T> implements Named, HelpProvider<T> {

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

	public void setPostArgument() {
		lookup = false;
		priority = -50;
	}
	
	public boolean isPostArgument() {
		return priority == -50;
	}
	
	public boolean hasDispatch() {
		return pipeline.getComponents().stream().anyMatch(c -> c instanceof DispatchComponent);
	}
	
	public boolean canLookup() {
		return lookup;
	}

	public boolean isRoot() {
		return getParent() == null;
	}

	public CommandPipeline<T> getPipeline() {
		return pipeline;
	}

	public void preparePipeline(CommandManager<T> manager) {
		processLookup(manager);
		pipeline.getComponents().forEach(c -> c.setParent(this));
		pipeline.prepare();
	}
	
	private void processLookup(CommandManager<T> manager) {
		List<Command<T>> subcommands = getSubcommands();
		subcommands.removeIf(c -> !c.canLookup());
		if (!subcommands.isEmpty()) {
			getPipeline().getComponents().removeAll(subcommands);
			SubcommandLookupComponent<T> lookup = manager.getComponentFactory().createLookupComponent(subcommands);
			getPipeline().addComponent(lookup);
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
		return priority;
	}

	public HelpComponent getHelpComponent() {
		return new HelpComponent(this, 10, getName());
	}

	@Override
	public void addHelp(HelpBuilder<T> help) {
		help.addHelp(getHelpComponent());
		for (CommandComponent<T> component : pipeline.getComponents()) {
			if (component instanceof HelpProvider) {
				((HelpProvider<T>) component).addHelp(help);
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
		CommandContext<T> clone = context.clone(this, 1, pipeline.getParsingSlots()).setParent(context);
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
		if (!postArgumentCompleteChecks(context)) {
			return success();
		}
		if (context.getArguments().size() == 1) {
			completions.addAll(names);
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
	
	private boolean postArgumentCompleteChecks(CommandContext<T> context) {
		if (!isPostArgument()) {
			return true;
		}
		Object[] parentParsed = context.getAllParsed();
		if (Arrays.stream(parentParsed).skip(1).anyMatch(Objects::isNull)) {
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return mainName;
	}

	public Set<String> getNames() {
		return names;
	}
	
	public void getParentPrefix(HelpPage<T> page, List<String> parts) {
		if (getParent() != null) {
			getParent().getParentPrefix(page, parts);
		}
		if (isPostArgument()) {
			parts.add(page.getHelp(getParent()).getUsage().split(" ", 2)[1]);
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
