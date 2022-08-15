package redempt.ordinate.spigot;

import org.bukkit.command.CommandSender;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.component.abstracts.HelpProvider;
import redempt.ordinate.data.CommandContext;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.help.HelpBuilder;
import redempt.ordinate.message.MessageFormatter;

import java.util.ArrayDeque;
import java.util.Queue;

class PermissionComponent extends CommandComponent<CommandSender> implements HelpProvider<CommandSender> {
	
	private String permission;
	private MessageFormatter<CommandSender> noPermissionError;
	
	public PermissionComponent(String permission, MessageFormatter<CommandSender> noPermissionError) {
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
		return 100;
	}
	
	@Override
	public CommandResult<CommandSender> parse(CommandContext<CommandSender> context) {
		return context.sender().hasPermission(permission) ? success() : failure(noPermissionError.format(context.sender(), permission)).complete();
	}
	
	@Override
	public void addHelp(HelpBuilder<CommandSender> help) {
		Queue<Command<CommandSender>> toExplore = new ArrayDeque<>();
		toExplore.add(getParent());
		while (!toExplore.isEmpty()) {
			Command<CommandSender> cmd = toExplore.poll();
			help.addFilter(cmd, p -> p.hasPermission(permission));
			toExplore.addAll(cmd.getSubcommands());
		}
	}
	
}
