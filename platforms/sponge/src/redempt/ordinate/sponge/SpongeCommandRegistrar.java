package redempt.ordinate.sponge;

import net.kyori.adventure.text.Component;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.ArgumentReader;
import org.spongepowered.plugin.PluginContainer;
import redempt.ordinate.command.CommandBase;
import redempt.ordinate.dispatch.CommandRegistrar;
import redempt.ordinate.help.HelpEntry;
import redempt.ordinate.help.HelpPage;

import java.util.*;
import java.util.stream.Collectors;

public class SpongeCommandRegistrar implements CommandRegistrar<Subject> {

    public static Command.Raw createSpongeCommand(CommandBase<Subject> command) {
        List<String> aliases = command.getNames();

        return new Command.Raw() {

            final HelpPage<Subject> help = command.getHelpPage();

            @Override
            public CommandResult process(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
                final String[] args = arguments.totalLength() == 0 ? new String[0] : arguments.input().split(" ");
                command.execute(cause, args);
                return CommandResult.success();
            }

            @Override
            public List<CommandCompletion> complete(CommandCause cause, ArgumentReader.Mutable arguments) throws CommandException {
                String[] args = arguments.input().isEmpty() ? new String[]{""} : arguments.input().split(" ");

                return command.getCompletions(cause, args).getCompletions()
                        .stream()
                        .map(CommandCompletion::of)
                        .collect(Collectors.toList());
            }

            @Override
            public boolean canExecute(CommandCause cause) {
                return true;
            }

            @Override
            public Optional<Component> shortDescription(CommandCause cause) {
                return Optional.empty();
            }

            @Override
            public Optional<Component> extendedDescription(CommandCause cause) {
                String description = command.getCommands().stream()
                        .map(help::getHelp)
                        .map(HelpEntry::getDescription)
                        .filter(Objects::nonNull)
                        .findFirst().orElse("");

                return Optional.of(Component.text(description));
            }

            @Override
            public Component usage(CommandCause cause) {
                return Component.text(command.getCommands().stream()
                        .map(help::getHelp)
                        .map(HelpEntry::getUsage)
                        .filter(Objects::nonNull)
                        .findFirst().orElse(""));
            }
        };
    }

    private final String fallbackPrefix;

    private final PluginContainer plugin;

    SpongeCommandRegistrar(PluginContainer plugin, String fallbackPrefix) {
        this.fallbackPrefix = fallbackPrefix;
        this.plugin = plugin;
    }

    private final Map<CommandBase<Subject>, Command.Raw> commandMap = new HashMap<>();

    @Override
    public void register(CommandBase<Subject> command) {
        Command.Raw raw = createSpongeCommand(command);
        commandMap.put(command, raw);
    }

    @Listener(order = Order.LAST)
    public void registerCommandEvent(RegisterCommandEvent<Command.Raw> event) {
        commandMap.forEach((cmd, raw) -> {
            event.register(plugin, raw, cmd.getName(), cmd.getNames().toArray(new String[0]));
        });
    }

    @Override
    public void unregister(CommandBase<Subject> command) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Unable to unregister commands");
    }
}
