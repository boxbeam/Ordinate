package redempt.ordinate.sponge;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.registry.RegistryType;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.world.WorldType;
import org.spongepowered.plugin.PluginContainer;
import redempt.ordinate.builder.BuilderOptions;
import redempt.ordinate.builder.CommandBuilder;
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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

public class SpongeCommandManager implements CommandManager<Subject> {

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

    public static <V> ContextProvider<Subject, V> playerContext(String name, String error, Function<ServerPlayer, V> provider) {
        return ContextProvider.create(name, error, ctx -> {
            if (!(ctx.sender() instanceof ServerPlayer)) {
                return null;
            }
            return provider.apply((ServerPlayer) ctx.sender());
        });
    }

    public static SpongeCommandManager getInstance(PluginContainer plugin, String fallbackPrefix, Properties messages) {
        MessageProvider<Subject> messageProvider = new PropertiesMessageProvider<>(messages, (subject, s) -> getAudience(subject).sendMessage(Component.text(s)), FormatUtils::color);
        return new SpongeCommandManager(plugin, fallbackPrefix, messageProvider);
    }

    public static SpongeCommandManager getInstance(PluginContainer plugin, String fallbackPrefix) {
        return getInstance(plugin, fallbackPrefix, getDefaultMessages());
    }

    public static SpongeCommandManager getInstance(PluginContainer plugin, Properties messages) {
        String name = plugin.metadata().name().orElse("");
        return getInstance(plugin, name.toLowerCase(), messages);
    }

    public static SpongeCommandManager getInstance(PluginContainer plugin) {
        String name = plugin.metadata().name().orElse("");
        return getInstance(plugin, name.toLowerCase(), getDefaultMessages());
    }

    private static Audience getAudience(Subject subject) {
        return (Audience) subject.contextCause().root();
    }

    private String fallbackPrefix;
    private CommandRegistrar<Subject> registrar;
    private ComponentFactory<Subject> componentFactory;
    private MessageProvider<Subject> messages;
    private HelpDisplayer<Subject> helpDisplayer;
    private BuilderOptions<Subject> builderOptions = BuilderOptions.getDefaults();
    private PluginContainer plugin;

    protected SpongeCommandManager(PluginContainer plugin, String fallbackPrefix, MessageProvider<Subject> messages) {
        this.fallbackPrefix = fallbackPrefix;
        this.messages = messages;
        registrar = new SpongeCommandRegistrar(plugin, fallbackPrefix);
        componentFactory = new DefaultComponentFactory<>(messages);
        helpDisplayer = new SpongeHelpDisplayer(getCommandPrefix(), messages);
        this.plugin = plugin;
        applyBuilderTypes();
        Sponge.eventManager().registerListeners(plugin, SpongeCommandRegistrar.class);
    }

    public SpongeCommandManager loadMessages() {
        URI uri = plugin.locateResource(URI.create("command-messages.properties")).get();
        return loadMessages(Paths.get(uri));
    }

    public MessageProvider<Subject> getMessages() {
        return messages;
    }

    public SpongeCommandManager loadMessages(Path path) {
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
        messages = new PropertiesMessageProvider<>(loaded, (subject, s) -> getAudience(subject).sendMessage(Component.text(s)), FormatUtils::color);
        componentFactory = new DefaultComponentFactory<>(messages);
        helpDisplayer = new SpongeHelpDisplayer(getCommandPrefix(), messages);
        return this;
    }

    @Override
    public CommandRegistrar<Subject> getRegistrar() {
        return registrar;
    }

    @Override
    public HelpDisplayer<Subject> getHelpDisplayer() {
        return helpDisplayer;
    }

    @Override
    public ComponentFactory<Subject> getComponentFactory() {
        return componentFactory;
    }

    @Override
    public CommandParser<Subject> getParser() {
        ParserOptions<Subject> parserOptions = ParserOptions.getDefaults(getComponentFactory());
        CommandParser<Subject> parser = new CommandParser<>(parserOptions, this);
        applyTagProcessors(parser);
        applyArgTypes(parser);
        applyContextProviders(parser);
        return parser;
    }

    private void applyTagProcessors(CommandParser<Subject> parser) {
        parser.addTagProcessors(
                TagProcessor.create("permission", (command, arg) -> {
                    command.getPipeline().addComponent(new PermissionComponent(arg, messages.getFormatter("noPermission")));
                }),
                TagProcessor.create("playerOnly", (command, arg) -> {
                    command.getPipeline().addComponent(new PlayerOnlyComponent(messages.getFormatter("playerOnly")));
                })
        );
    }

    private void applyBuilderTypes() {
        builderOptions.addType(ServerPlayer.class, s -> Sponge.server().player(s).get()).completerStream((ctx, val) -> Sponge.server().onlinePlayers().stream().map(ServerPlayer::name));

        builderOptions.addType(ItemType.class, s -> {
            ResourceKey key = ResourceKey.resolve(s);
            return Sponge.game().registry(RegistryTypes.ITEM_TYPE).findValue(key).get();
        }).completerStream((ctx, val) -> Sponge.game().registry(RegistryTypes.ITEM_TYPE).streamEntries().map(reg -> reg.key().formatted()));
    }


    private void applyArgTypes(CommandParser<Subject> parser) {
        parser.addArgTypes(new ArgType<>("player", Sponge.server()::player).completerStream((ctx, val) -> Sponge.server().onlinePlayers().stream().map(ServerPlayer::name)));

        parser.addArgTypes(new ArgType<>("world", t -> {
            ResourceKey key = ResourceKey.resolve(t);
            return Sponge.game().registry(RegistryTypes.WORLD_TYPE).findValue(key);
        }).completerStream((ctx, val) -> Sponge.game().registry(RegistryTypes.WORLD_TYPE).streamEntries().map(reg -> reg.key().formatted())));

        parser.addArgTypes(new ArgType<>("item", s -> {
            ResourceKey key = ResourceKey.resolve(s);
            return Sponge.game().registry(RegistryTypes.ITEM_TYPE).findValue(key);
        }).completerStream((ctx, val) -> Sponge.game().registry(RegistryTypes.ITEM_TYPE).streamEntries().map(reg -> reg.key().formatted())));
    }

    private void applyContextProviders(CommandParser<Subject> parser) {
        parser.addContextProviders(playerContext("self", messages.getFormatter("playerOnly").format(null).toString(), p -> p));
    }

    @Override
    public CommandBuilder<Subject, ?> builder(String... names) {
        return new SpongeCommandBuilder(names, this, builderOptions);
    }

    @Override
    public String getCommandPrefix() {
        return "/";
    }
}
