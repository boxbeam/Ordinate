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
		Map<Command<?>, HelpEntry> entries = new LinkedHashMap<>();
		map.forEach((cmd, components) -> entries.put(cmd, createEntry(cmd, components)));
		return new HelpPage(entries);
	}

	private HelpEntry createEntry(Command<?> owner, List<HelpComponent> components) {
		components.sort(Comparator.comparingInt(h -> -h.getPriority()));
		List<HelpComponent> prefix = new ArrayList<>();
		Command<?> cmd = owner;
		while (cmd.getParent() != null) {
			prefix.add(cmd.getHelpComponent());
			cmd = cmd.getParent();
		}
		Collections.reverse(prefix);
		prefix.addAll(components);
		return new HelpEntry(owner, components);
	}

}
