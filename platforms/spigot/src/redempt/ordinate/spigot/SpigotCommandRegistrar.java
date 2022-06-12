package redempt.ordinate.spigot;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import redempt.ordinate.command.CommandBase;
import redempt.ordinate.dispatch.CommandRegistrar;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class SpigotCommandRegistrar implements CommandRegistrar<CommandSender> {
	
	private Map<String, Command> knownCommands;
	private CommandMap commandMap;
	private String fallbackPrefix;
	
	public SpigotCommandRegistrar(String fallbackPrefix) {
		this.fallbackPrefix = fallbackPrefix;
		initKnownCommands();
	}
	
	private void initKnownCommands() {
		try {
			Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
			field.setAccessible(true);
			commandMap = (SimpleCommandMap) field.get(Bukkit.getPluginManager());
			field = SimpleCommandMap.class.getDeclaredField("knownCommands");
			field.setAccessible(true);
			knownCommands = (Map<String, Command>) field.get(commandMap);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	private Command createSpigotCommand(CommandBase<CommandSender> command) {
		List<String> aliases = command.getNames();
		return new Command(command.getCommands().get(0).getName()) {
			@Override
			public boolean execute(CommandSender sender, String commandLabel, String[] args) {
				command.execute(sender, args);
				return false;
			}
			
			@Override
			public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
				return command.getCompletions(sender, args).getCompletions();
			}
			
			@Override
			public List<String> getAliases() {
				return aliases;
			}
		};
	}
	
	@Override
	public void register(CommandBase<CommandSender> command) {
		Command cmd = createSpigotCommand(command);
		command.getCommands().forEach(c -> commandMap.register(fallbackPrefix, cmd));
	}
	
	@Override
	public void unregister(CommandBase<CommandSender> command) {
		for (String name : command.getNames()) {
			knownCommands.remove(name);
			knownCommands.remove(fallbackPrefix + ":" + name);
		};
	}
	
}
