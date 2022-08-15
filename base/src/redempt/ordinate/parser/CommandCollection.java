package redempt.ordinate.parser;

import redempt.ordinate.command.Command;
import redempt.ordinate.command.CommandBase;
import redempt.ordinate.dispatch.CommandManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a collection of parsed commands ready to be registered
 * @param <T> The sender type
 * @author Redempt
 */
public class CommandCollection<T> {

	private List<CommandBase<T>> commands = new ArrayList<>();
	private CommandManager<T> commandManager;

	protected CommandCollection(List<Command<T>> commands, CommandManager<T> commandManager) {
		Map<String, List<Command<T>>> commandsByName = new HashMap<>();
		commands.forEach(cmd -> commandsByName.computeIfAbsent(cmd.getName(), k -> new ArrayList<>()).add(cmd));
		commandsByName.forEach((name, cmds) -> this.commands.add(new CommandBase<>(cmds, commandManager)));
		this.commandManager = commandManager;
	}
	
	/**
	 * Registers all commands in the collection
	 */
	public void register() {
		for (CommandBase<T> cmd : commands) {
			commandManager.getRegistrar().register(cmd);
		}
	}
	
	/**
	 * @return The parsed commands
	 */
	public List<CommandBase<T>> getCommands() {
		return commands;
	}

}
