package redempt.ordinate.sponge;

import org.spongepowered.api.command.CommandCause;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.message.MessageFormatter;

import java.util.ArrayDeque;
import java.util.Queue;

public class PermissionComponent extends CommandComponent<CommandCause> implements HelpProvider<CommandCause> {

    private final String permission;

    private final MessageFormatter<CommandCause> noPermissionError;

    public PermissionComponent(String permission, MessageFormatter<CommandCause> noPermissionError) {
        this.permission = permission;
        this.noPermissionError = noPermissionError;
    }

    @Override
    public int getMaxConsumedArgs() {
        return 0;
    }

    @Override
    public int getMaxParsedObjects() {
        return 0;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public CommandResult<CommandCause> parse(CommandContext<CommandCause> context) {
        return context.sender().hasPermission(permission) ? success() : failure(noPermissionError.format(context.sender(), permission)).complete();
    }

    @Override
    public void addHelp(HelpBuilder<CommandCause> help) {
        Queue<Command<CommandCause>> toExplore = new ArrayDeque<>();
        toExplore.add(getParent());
        while (!toExplore.isEmpty()) {
            Command<CommandCause> cmd = toExplore.poll();
            help.addFilter(cmd, p -> p.hasPermission(permission));
            toExplore.addAll(cmd.getSubcommands());
        }
    }
}
