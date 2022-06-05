package redempt.ordinate.help;

import redempt.ordinate.command.Command;

import java.util.*;

public class HelpBuilder {

	private List<HelpComponent> components = new ArrayList<>();

	public void addHelp(HelpComponent component) {
		components.add(component);
	}

	public HelpPage build() {
		Map<Command<?>, List<HelpComponent>> map = new HashMap<>();
		for (HelpComponent component : components) {
			Command<?> cmd = component.getOwner() instanceof Command ? (Command<?>) component.getOwner() : component.getOwner().getParent();
			map.computeIfAbsent(cmd, k -> new ArrayList<>()).add(component);
		}
		map.values().forEach(list -> list.sort(Comparator.comparingInt(h -> -h.getPriority())));
		Map<Command<?>, HelpEntry> entries = new HashMap<>();
		map.forEach((cmd, components) -> entries.put(cmd, new HelpEntry(cmd, components)));
		return new HelpPage(entries);
	}

}
