package redempt.ordinate.spigot;

import org.bukkit.command.CommandSender;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.dispatch.CommandRegistrar;
import redempt.ordinate.dispatch.MessageDispatcher;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.parser.CommandParser;
import redempt.ordinate.parser.metadata.ParserOptions;

public class SpigotCommandManager implements CommandManager<CommandSender> {
	
	private String fallbackPrefix;
	private CommandRegistrar<CommandSender> registrar;
	
	private SpigotCommandManager(String fallbackPrefix) {
		this.fallbackPrefix = fallbackPrefix;
		registrar = new SpigotCommandRegistrar(fallbackPrefix);
	}
	
	@Override
	public CommandRegistrar<CommandSender> getRegistrar() {
		return registrar;
	}
	
	@Override
	public HelpDisplayer<CommandSender> getHelpDisplayer() {
		return (sender, entry) -> sender.sendMessage(entry.toString());
	}
	
	@Override
	public MessageDispatcher<CommandSender> getMessageDispatcher() {
		return CommandSender::sendMessage;
	}
	
	@Override
	public ComponentFactory<CommandSender> getComponentFactory() {
		return null;
	}
	
	@Override
	public CommandParser<CommandSender> getCommandParser() {
		ParserOptions<CommandSender> parserOptions = ParserOptions.getDefaults(getComponentFactory());
		return new CommandParser<>(parserOptions, this);
	}
	
	@Override
	public String getCommandPrefix() {
		return "/";
	}
	
	public String getFallbackPrefix() {
		return fallbackPrefix;
	}
	
}
