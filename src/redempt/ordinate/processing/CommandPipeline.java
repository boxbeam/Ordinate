package redempt.ordinate.processing;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;

import java.util.*;
import java.util.function.Supplier;

public class CommandPipeline<T> {

	private List<CommandComponent<T>> components = new ArrayList<>();
	private Comparator<CommandComponent<T>> comparator;
	private boolean finalized = false;
	private int parsingSlots;
	private int maxArgWidth;
	private int minArgWidth;

	public CommandPipeline() {
		this(Collections.emptyList());
	}

	public CommandPipeline(Collection<CommandComponent<T>> components) {
		this.components.addAll(components);
		comparator = Comparator.comparingInt(CommandComponent::getPriority);
		comparator = comparator.reversed().thenComparing(CommandComponent::getIndex);
		long maxArgWidth = 0;
		for (CommandComponent<T> component : this.components) {
			parsingSlots += component.getMaxParsedObjects();
			maxArgWidth += component.getMaxConsumedArgs();
			minArgWidth += component.getMinConsumedArgs();

		}
		this.maxArgWidth = (int) Math.min(Integer.MAX_VALUE, maxArgWidth);
	}

	public List<CommandComponent<T>> getComponents() {
		return components;
	}

	public boolean removeComponent(CommandComponent<T> component) {
		if (!components.remove(component)) {
			return false;
		}
		parsingSlots -= component.getMaxParsedObjects();
		maxArgWidth -= component.getMaxConsumedArgs();
		minArgWidth -= component.getMinConsumedArgs();
		return true;
	}

	public void addComponent(CommandComponent<T> component) {
		if (finalized) {
			throw new IllegalStateException("Pipeline already finalized, cannot add more components");
		}
		components.add(component);
		parsingSlots += component.getMaxParsedObjects();
		maxArgWidth += component.getMaxConsumedArgs();
		minArgWidth += component.getMinConsumedArgs();
	}

	public void prepare() {
		if (finalized) {
			throw new IllegalStateException("Pipeline already finalized, cannot add more components");
		}
		finalized = true;
		components.sort(comparator);
		int index = 0;
		for (CommandComponent<T> component : components) {
			component.setIndex(index);
			index += component.getMaxParsedObjects();
		}
		components = Collections.unmodifiableList(components);
	}

	public CommandResult<T> parse(CommandContext<T> context, Supplier<CommandResult<T>> defaultResult) {
		CommandResult<T> deepestError = null;
		for (CommandComponent<T> component : components) {
			CommandResult<T> result = component.parse(context);
			if (result.isComplete()) {
				return result.isSuccess() ? result : CommandResult.deepest(deepestError, result);
			}
			if (!result.isSuccess()) {
				deepestError = CommandResult.deepest(deepestError, result);
			}
		}
		return deepestError == null ? defaultResult.get() : deepestError;
	}

	public Set<String> completions(CommandContext<T> context) {
		Set<String> completions = new HashSet<>();
		for (CommandComponent<T> component : components) {
			CommandResult<T> result = component.complete(context, completions);
			if (!result.isSuccess()) {
				return completions;
			}
		}
		return completions;
	}

	public int getMaxArgWidth() {
		return maxArgWidth;
	}

	public int getMinArgWidth() {
		return minArgWidth;
	}

	public int getParsingSlots() {
		return parsingSlots;
	}

}
