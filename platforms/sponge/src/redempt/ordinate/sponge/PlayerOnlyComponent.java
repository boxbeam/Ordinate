package redempt.ordinate.sponge;

import org.spongepowered.api.command.CommandCause;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.message.MessageFormatter;

public class PlayerOnlyComponent extends CommandComponent<CommandCause> {

    private final MessageFormatter<CommandCause> error;

    public PlayerOnlyComponent(MessageFormatter<CommandCause> error) { this.error = error; }


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
        if (context.sender() instanceof ServerPlayer) {
            return success();
        }
        return failure(error.format(context.sender())).complete();
    }
}
