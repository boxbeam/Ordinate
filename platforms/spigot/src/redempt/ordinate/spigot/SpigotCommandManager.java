package redempt.ordinate.spigot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.creation.DefaultComponentFactory;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.dispatch.CommandRegistrar;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.message.MessageProvider;
import redempt.ordinate.message.PropertiesMessageProvider;
import redempt.ordinate.parser.CommandParser;
import redempt.ordinate.parser.TagProcessor;
import redempt.ordinate.parser.metadata.ParserOptions;

import java.util.Properties;

public class SpigotCommandManager implements CommandManager<CommandSender> {
	
	public static Properties getDefaultMessages() {
		Properties props = new Properties();
		props.setProperty("missingArgument", "&cMissing value for argument: %1");
		props.setProperty("invalidArgumentValue", "&cInvalid value for argument %1: %2");
		props.setProperty("executionFailed", "&cCommand execution failed due to an unexpected error. Please report this to an administrator.");
		props.setProperty("tooManyArguments", "&cToo many arguments: Extra %1 argument(s) provided");
		props.setProperty("numberOutsideRange", "&cNumber %1 outside range: %2");
		props.setProperty("contextError", "&c%1");
		props.setProperty("constraintError", "&cConstraint failed for %1: %2");
		props.setProperty("invalidSubcommand", "&cInvalid subcommand: %1");
		props.setProperty("noPermission", "&cYou do not have permission to do that (%1)");
		props.setProperty("playerOnly", "&cThis command must be executed as a player");
		return props;
	}
	
	public static SpigotCommandManager getInstance(Plugin plugin, String fallbackPrefix, Properties messages) {
		MessageProvider<CommandSender> messageProvider = new PropertiesMessageProvider<>(messages, CommandSender::sendMessage, FormatUtils::color);
		return new SpigotCommandManager(plugin, fallbackPrefix, messageProvider);
	}
	
	public static SpigotCommandManager getInstance(Plugin plugin, String fallbackPrefix) {
		return getInstance(plugin, fallbackPrefix, getDefaultMessages());
	}
	
	public static SpigotCommandManager getInstance(Plugin plugin, Properties messages) {
		return getInstance(plugin, plugin.getName().toLowerCase(), messages);
	}
	
	public static SpigotCommandManager getInstance(Plugin plugin) {
		return getInstance(plugin, plugin.getName().toLowerCase(), getDefaultMessages());
	}
	
	private String fallbackPrefix;
	private CommandRegistrar<CommandSender> registrar;
	private ComponentFactory<CommandSender> componentFactory;
	private MessageProvider<CommandSender> messages;
	
	private SpigotCommandManager(Plugin plugin, String fallbackPrefix, MessageProvider<CommandSender> messages) {
		this.fallbackPrefix = fallbackPrefix;
		this.messages = messages;
		registrar = new SpigotCommandRegistrar(plugin, fallbackPrefix);
		componentFactory = new DefaultComponentFactory<>(messages);
	}
	
	@Override
	public CommandRegistrar<CommandSender> getRegistrar() {
		return registrar;
	}
	
	@Override
	public HelpDisplayer<CommandSender> getHelpDisplayer() {
		return (sender, entry) -> {
			sender.sendMessage(getCommandPrefix() + entry.toString());
		};
	}
	
	@Override
	public ComponentFactory<CommandSender> getComponentFactory() {
		return componentFactory;
	}
	
	@Override
	public CommandParser<CommandSender> getCommandParser() {
		ParserOptions<CommandSender> parserOptions = ParserOptions.getDefaults(getComponentFactory());
		CommandParser<CommandSender> parser = new CommandParser<>(parserOptions, this);
		applyTagProcessors(parser);
		return parser;
	}
	
	private void applyTagProcessors(CommandParser<CommandSender> parser) {
		parser.addTagProcessors(
				TagProcessor.create("permission", (command, arg) -> {
					command.getPipeline().addComponent(new PermissionComponent(arg, messages.getFormatter("noPermission")));
					return command;
				}),
				TagProcessor.create("playerOnly", (command, arg) -> {
					command.getPipeline().addComponent(new PlayerOnlyComponent(messages.getFormatter("playerOnly")));
					return command;
				})
		);
	}
	
	@Override
	public String getCommandPrefix() {
		return "/";
	}
	
	public SpigotCommandManager setFallbackPrefix(String fallbackPrefix) {
		this.fallbackPrefix = fallbackPrefix;
		return this;
	}
	
	public String getFallbackPrefix() {
		return fallbackPrefix;
	}
	
}
