package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.*;
import java.util.function.Predicate;

public class HelpBuilder<T> {

	private List<HelpComponent> components = new ArrayList<>();
	private Map<Command<T>, List<Predicate<T>>> filters = new HashMap<>();
	
	public void addHelp(HelpComponent component) {
		components.add(component);
	}

	public void addFilter(Command<T> owner, Predicate<T> filter) {
		filters.computeIfAbsent(owner, k -> new ArrayList<>()).add(filter);
	}
	
	public HelpPage<T> build() {
		Map<Command<T>, List<HelpComponent>> map = new HashMap<>();
		for (HelpComponent component : components) {
			Command<T> cmd = (Command<T>) (component.getOwner() instanceof Command ? component.getOwner() : component.getOwner().getParent());
			map.computeIfAbsent(cmd, k -> new ArrayList<>()).add(component);
		}
		Map<Command<T>, HelpEntry<T>> entries = new LinkedHashMap<>();
		map.forEach((cmd, components) -> entries.put(cmd, createEntry(cmd, components)));
		return new HelpPage(entries);
	}

	private HelpEntry<T> createEntry(Command<T> owner, List<HelpComponent> components) {
		components.sort(Comparator.comparingInt(h -> -h.getPriority()));
		List<HelpComponent> prefix = new ArrayList<>();
		Command<?> cmd = owner;
		while (cmd.getParent() != null) {
			prefix.add(cmd.getHelpComponent());
			cmd = cmd.getParent();
		}
		Collections.reverse(prefix);
		prefix.addAll(components);
		HelpEntry<T> entry = new HelpEntry<>(owner, components);
		filters.getOrDefault(owner, Collections.emptyList()).forEach(entry::addFilter);
		return entry;
	}

}
