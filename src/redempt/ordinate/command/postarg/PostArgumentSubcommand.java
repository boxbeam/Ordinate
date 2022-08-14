package redempt.ordinate.command.postarg;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class PostArgumentSubcommand {

	public static <T> void makePostArgument(Command<T> command) {
		if (command.isRoot()) {
			throw new IllegalStateException("Base command " + command.getName() + " may not be post-argument");
		}
		command.setPostArgument();
		int count = command.getParent().getPipeline().getComponents().stream().mapToInt(CommandComponent::getMaxParsedObjects).sum();
		count -= Optional.ofNullable(getPropagationComponent(command)).map(ArgumentPropagationComponent::getPull).orElse(0);
		if (count == 0) {
			throw new IllegalStateException("Post-argument command " + command.getName() + "'s parent has no arguments");
		}
		Queue<Command<T>> queue = new ArrayDeque<>();
		queue.add(command);
		while (!queue.isEmpty()) {
			Command<T> next = queue.poll();
			ArgumentPropagationComponent<T> component = createPropagationComponent(next);
			component.setPull(component.getPull() + count);
			queue.addAll(next.getSubcommands());
		}
	}
	
	public static <T> ArgumentPropagationComponent<T> getPropagationComponent(Command<T> cmd) {
		return (ArgumentPropagationComponent<T>) cmd.getPipeline().getComponents().stream()
				.filter(c -> c instanceof ArgumentPropagationComponent)
				.findFirst().orElse(null);
	}
	
	private static <T> ArgumentPropagationComponent<T> createPropagationComponent(Command<T> cmd) {
		ArgumentPropagationComponent<T> component = getPropagationComponent(cmd);
		if (component == null) {
			component = new ArgumentPropagationComponent<>();
			cmd.getPipeline().addComponent(component);
		}
		return component;
	}
	
}
