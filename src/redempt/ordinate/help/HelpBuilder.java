package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.*;
import java.util.function.Predicate;

public class HelpBuilder<T> {
	
	Map<Command<T>, List<HelpComponent>> map = new HashMap<>();
	private Map<Command<T>, List<Predicate<T>>> filters = new HashMap<>();
	
	public void addHelp(HelpComponent component) {
		Command<T> cmd = (Command<T>) (component.getOwner() instanceof Command ? component.getOwner() : component.getOwner().getParent());
		map.computeIfAbsent(cmd, k -> new ArrayList<>()).add(component);
	}

	public void addFilter(Command<T> owner, Predicate<T> filter) {
		filters.computeIfAbsent(owner, k -> new ArrayList<>()).add(filter);
	}
	
	public HelpPage<T> build() {
		Map<Command<T>, HelpEntry<T>> entries = new LinkedHashMap<>();
		map.forEach((cmd, components) -> entries.put(cmd, createEntry(cmd, components)));
		return new HelpPage<>(entries);
	}

	public HelpEntry<T> getPartialEntry(Command<T> cmd) {
		return cmd == null ? null : createEntry(cmd, map.get(cmd));
	}
	
	private HelpEntry<T> createEntry(Command<T> owner, List<HelpComponent> components) {
		components.sort(Comparator.comparingInt(h -> -h.getPriority()));
		HelpEntry<T> entry = new HelpEntry<>(owner, components);
		filters.getOrDefault(owner, Collections.emptyList()).forEach(entry::addFilter);
		return entry;
	}

}
