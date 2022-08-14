package redempt.ordinate.spigot;

import org.bukkit.command.CommandSender;
import redempt.ordinate.builder.BuilderOptions;
import redempt.ordinate.builder.CommandBuilder;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.message.MessageFormatter;

public class SpigotCommandBuilder extends CommandBuilder<CommandSender, SpigotCommandBuilder> {
	
	public SpigotCommandBuilder(String[] names, CommandManager<CommandSender> manager, BuilderOptions<CommandSender> options) {
		super(names, manager, options, SpigotCommandBuilder::new);
	}
	
	private SpigotCommandManager getManager() {
		return (SpigotCommandManager) manager;
	}
	
	public SpigotCommandBuilder permission(String permission) {
		MessageFormatter<CommandSender> msg = getManager().getMessages().getFormatter("noPermission");
		pipeline.addComponent(new PermissionComponent(permission, msg));
		return this;
	}
	
	public SpigotCommandBuilder playerOnly() {
		MessageFormatter<CommandSender> msg = getManager().getMessages().getFormatter("playerOnly");
		pipeline.addComponent(new PlayerOnlyComponent(msg));
		return this;
	}
	
}
