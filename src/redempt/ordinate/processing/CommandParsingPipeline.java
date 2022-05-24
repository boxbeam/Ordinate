package redempt.ordinate.processing;

import redempt.ordinate.component.CommandComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class CommandParsingPipeline<T> {

	private List<CommandComponent<T>> components = new ArrayList<>();
	private Comparator<CommandComponent<T>> comparator;
	private boolean finalized = false;
	private int parsingSlots;
	private int argWidth;

	public CommandParsingPipeline(Collection<CommandComponent<T>> components) {
		this.components.addAll(components);
		comparator = Comparator.comparingInt(CommandComponent::getPriority);
		comparator = comparator.reversed().thenComparing(CommandComponent::getIndex);
		long argWidth = 0;
		for (CommandComponent<T> component : this.components) {
			parsingSlots += component.getMaxParsedObjects();
			argWidth += component.getMaxConsumedArgs();
		}
		this.argWidth = (int) Math.min(Integer.MAX_VALUE, argWidth);
	}

	public void addComponent(CommandComponent<T> component) {
		if (finalized) {
			throw new IllegalStateException("Pipeline already finalized, cannot add more components");
		}
		components.add(component);
		parsingSlots += component.getMaxParsedObjects();
		argWidth += component.getMaxConsumedArgs();
	}

	public void prepare() {
		if (finalized) {
			throw new IllegalStateException("Pipeline already finalized, cannot add more components");
		}
		finalized = true;
		components.sort(comparator);
	}

}
