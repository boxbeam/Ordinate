package redempt.ordinate.spigot;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import redempt.ordinate.command.ArgType;
import redempt.ordinate.context.ContextProvider;
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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Function;

public class SpigotCommandManager implements CommandManager<CommandSender> {
	
	public static Properties getDefaultMessages() {
		Properties props = new Properties();
		props.setProperty("missingArgument", "&cMissing required argument: %1");
		props.setProperty("invalidArgumentValue", "&cInvalid value for argument %1: %2");
		props.setProperty("executionFailed", "&cCommand execution failed due to an unexpected error. Please report this to an administrator.");
		props.setProperty("tooManyArguments", "&cToo many arguments: Extra %1 argument(s) provided");
		props.setProperty("numberOutsideRange", "&cNumber %1 outside range: %2");
		props.setProperty("contextError", "&c%1");
		props.setProperty("constraintError", "&cConstraint failed for %1: %2");
		props.setProperty("invalidSubcommand", "&cInvalid subcommand: %1");
		props.setProperty("noPermission", "&cYou do not have permission to do that (%1)");
		props.setProperty("playerOnly", "&cThis command must be executed as a player");
		props.setProperty("helpFormat", "&e%1 &7%2");
		return props;
	}
	
	public static <V> ContextProvider<CommandSender, V> playerContext(String name, String error, Function<Player, V> provider) {
		return ContextProvider.create(name, error, ctx -> {
			if (!(ctx.sender() instanceof Player)) {
				return null;
			}
			return provider.apply((Player) ctx.sender());
		});
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
	private HelpDisplayer<CommandSender> helpDisplayer;
	private Plugin plugin;
	
	private SpigotCommandManager(Plugin plugin, String fallbackPrefix, MessageProvider<CommandSender> messages) {
		this.fallbackPrefix = fallbackPrefix;
		this.messages = messages;
		registrar = new SpigotCommandRegistrar(plugin, fallbackPrefix);
		componentFactory = new DefaultComponentFactory<>(messages);
		helpDisplayer = new SpigotHelpDisplayer(getCommandPrefix(), messages);
		this.plugin = plugin;
	}
	
	public SpigotCommandManager loadMessages() {
		return loadMessages(plugin.getDataFolder().toPath().resolve("command-messages.properties"));
	}
	
	public MessageProvider<CommandSender> getMessages() {
		return messages;
	}
	
	public SpigotCommandManager loadMessages(Path path) {
		Properties loaded = getDefaultMessages();
		try {
			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());
				Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE);
				loaded.store(writer, null);
				writer.close();
			}
			Reader reader = Files.newBufferedReader(path);
			loaded.load(reader);
			reader.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		messages = new PropertiesMessageProvider<>(loaded, CommandSender::sendMessage, FormatUtils::color);
		componentFactory = new DefaultComponentFactory<>(messages);
		helpDisplayer = new SpigotHelpDisplayer(getCommandPrefix(), messages);
		return this;
	}
	
	@Override
	public CommandRegistrar<CommandSender> getRegistrar() {
		return registrar;
	}
	
	@Override
	public HelpDisplayer<CommandSender> getHelpDisplayer() {
		return helpDisplayer;
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
		applyArgTypes(parser);
		applyContextProviders(parser);
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
	
	private void applyArgTypes(CommandParser<CommandSender> parser) {
		parser.addArgTypes(new ArgType<>("player", Bukkit::getPlayerExact).completerStream((ctx, val) -> Bukkit.getOnlinePlayers().stream().map(Player::getName)));
		parser.addArgTypes(new ArgType<>("world", Bukkit::getWorld).completerStream((ctx, val) -> Bukkit.getWorlds().stream().map(World::getName)));
		parser.addArgTypes(new ArgType<>("material", s -> Material.getMaterial(s)).completerStream((ctx, val) -> Arrays.stream(Material.values()).map(Material::name)));
	}
	
	private void applyContextProviders(CommandParser<CommandSender> parser) {
		parser.addContextProviders(playerContext("self", messages.getFormatter("playerOnly").format(null).toString(), p -> p));
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
