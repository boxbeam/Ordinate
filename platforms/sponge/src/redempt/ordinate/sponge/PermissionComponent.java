package redempt.ordinate.sponge;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.service.permission.Subject;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.message.MessageFormatter;

import java.util.ArrayDeque;
import java.util.Queue;

public class PermissionComponent extends CommandComponent<Subject> implements HelpProvider<Subject> {

    private String permission;

    private MessageFormatter<Subject> noPermissionError;

    public PermissionComponent(String permission, MessageFormatter<Subject> noPermissionError) {
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
    public CommandResult<Subject> parse(CommandContext<Subject> context) {
        return context.sender().hasPermission(permission) ? success() : failure(noPermissionError.format(context.sender(), permission)).complete();
    }

    @Override
    public void addHelp(HelpBuilder<Subject> help) {
        Queue<Command<Subject>> toExplore = new ArrayDeque<>();
        toExplore.add(getParent());
        while (!toExplore.isEmpty()) {
            Command<Subject> cmd = toExplore.poll();
            help.addFilter(cmd, p -> p.hasPermission(permission));
            toExplore.addAll(cmd.getSubcommands());
        }
    }
}
