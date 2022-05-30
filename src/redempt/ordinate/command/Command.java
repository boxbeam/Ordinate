package redempt.ordinate.command;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.*;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.help.DelimitedHelpComponent;
import redempt.ordinate.help.HelpComponent;
import redempt.ordinate.help.LiteralHelpComponent;
import redempt.ordinate.processing.ArgumentSplitter;
import redempt.ordinate.processing.CachedSupplier;
import redempt.ordinate.processing.CommandParsingPipeline;

import java.util.*;
import java.util.function.Supplier;

public class Command<T> extends CommandComponent<T> implements Named, HelpProvider {

	private Command<T> parent;
	private String mainName;
	private Set<String> names = new HashSet<>();
	private CommandParsingPipeline<T> pipeline;
	private HelpComponent helpPage;
	private HelpComponent helpEntry;
	private String commandPrefix;
	private int priority = 20;

	public Command(String commandPrefix, String[] names, CommandParsingPipeline<T> pipeline) {
		mainName = names[0];
		Collections.addAll(this.names, names);
		this.pipeline = pipeline;
		this.commandPrefix = commandPrefix;
	}

	public CommandContext<T> createContext(T sender, String[] input, boolean forCompletions) {
		SplittableList<Argument> args = ArgumentSplitter.split(input, forCompletions);
		return new CommandContext<>(this, null, sender, args, pipeline.getParsingSlots());
	}

	public CommandContext<T> createContext(T sender, String input, boolean forCompletions) {
		SplittableList<Argument> args = ArgumentSplitter.split(input, forCompletions);
		return new CommandContext<>(this, null, sender, args, pipeline.getParsingSlots());
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
		List<HelpComponent> helpComponents = new ArrayList<>();
		HelpComponent rootComponent = new LiteralHelpComponent(this, 5, false, (isRoot() ? commandPrefix : "") + getName());
		helpComponents.add(rootComponent);
		List<HelpComponent> helpPage = new ArrayList<>();
		for (CommandComponent<T> component : pipeline.getComponents()) {
			component.setParent(this);
			if (component instanceof HelpProvider) {
				HelpComponent help = ((HelpProvider) component).getHelpComponent();
				if (help.isLine()) {
					help = new DelimitedHelpComponent(this, help.getPriority(), true, " ", rootComponent, help);
				}
				(help.isLine() ? helpPage : helpComponents).add(help);
			}
		}
		helpComponents.sort(Comparator.comparingInt(HelpComponent::getPriority));
		HelpComponent[] helpArray = helpComponents.toArray(new HelpComponent[0]);
		this.helpEntry = new DelimitedHelpComponent(this, 5, true, " ", helpArray);
		helpPage.add(0, helpEntry);
		HelpComponent[] helpPageArray = helpPage.toArray(new HelpComponent[0]);
		this.helpPage = new DelimitedHelpComponent(this, 0, true, "\n", helpPageArray);
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
		return helpEntry;
	}

	public HelpComponent getHelpPage() {
		return helpPage;
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
