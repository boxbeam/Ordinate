package redempt.ordinate.sponge;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.service.permission.Subject;
import redempt.ordinate.builder.BuilderOptions;
import redempt.ordinate.builder.CommandBuilder;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.message.MessageFormatter;

public class SpongeCommandBuilder extends CommandBuilder<Subject, SpongeCommandBuilder> {

    public SpongeCommandBuilder(String[] names, CommandManager<Subject> manager, BuilderOptions<Subject> options) {
        super(names,manager,options,SpongeCommandBuilder::new);
    }

    private SpongeCommandManager getManager() { return (SpongeCommandManager) manager; }

    public SpongeCommandBuilder permission(String permission) {
        MessageFormatter<Subject> msg = getManager().getMessages().getFormatter("noPermission");
        pipeline.addComponent(new PermissionComponent(permission, msg));
        return this;
    }

    public SpongeCommandBuilder playerOnly() {
        MessageFormatter<Subject> msg = getManager().getMessages().getFormatter("playerOnly");
        pipeline.addComponent(new PlayerOnlyComponent(msg));
        return this;
    }
}
