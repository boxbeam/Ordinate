package redempt.ordinate.parser;

import redempt.ordinate.command.Command;
import redempt.ordinate.dispatch.CommandManager;

import java.util.List;

public class CommandCollection<T> {

	private List<Command<T>> commands;
	private CommandManager<T> commandManager;

	protected CommandCollection(List<Command<T>> commands, CommandManager<T> commandManager) {
		this.commands = commands;
		this.commandManager = commandManager;
	}

	public void register() {
		for (Command<T> cmd : commands) {
			commandManager.getRegistrar().register(cmd);
		}
	}

	public List<Command<T>> getCommands() {
		return commands;
	}

}
