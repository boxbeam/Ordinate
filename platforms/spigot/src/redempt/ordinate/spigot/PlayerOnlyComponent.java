package redempt.ordinate.spigot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.message.MessageFormatter;

public class PlayerOnlyComponent extends CommandComponent<CommandSender> {
	
	private MessageFormatter<CommandSender> error;
	
	public PlayerOnlyComponent(MessageFormatter<CommandSender> error) {
		this.error = error;
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
		return 100;
	}
	
	@Override
	public CommandResult<CommandSender> parse(CommandContext<CommandSender> context) {
		if (context.sender() instanceof Player) {
			return success();
		}
		return failure(error.format(context.sender())).complete();
	}
	
}
