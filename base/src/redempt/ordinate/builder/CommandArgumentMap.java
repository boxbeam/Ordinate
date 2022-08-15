package redempt.ordinate.builder;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.Named;

import java.util.HashMap;
import java.util.Map;

public class CommandArgumentMap {

	public static Map<String, Integer> getMap(Command<?> command) {
		Map<String, Integer> map = new HashMap<>();
		putArgs(command, map);
		while (command.isPostArgument()) {
			int diff = command.getPipeline().getParsingSlots() - command.getParent().getPipeline().getParsingSlots();
			push(map, diff);
			command = command.getParent();
			putArgs(command, map);
		}
		return map;
	}
	
	private static void push(Map<String, Integer> map, int amount) {
		map.keySet().forEach(key -> map.compute(key, (k, v) -> v + amount));
	}
	
	private static void putArgs(Command<?> command, Map<String, Integer> map) {
		for (CommandComponent<?> component : command.getPipeline().getComponents()) {
			if (!(component instanceof Named) || component.getMaxParsedObjects() == 0) {
				continue;
			}
			String name = ((Named) component).getName();
			map.put(name, component.getIndex());
		}
	}

}
