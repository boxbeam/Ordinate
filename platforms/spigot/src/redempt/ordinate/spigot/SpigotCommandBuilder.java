package redempt.ordinate.spigot;

import org.bukkit.command.CommandSender;
import redempt.ordinate.builder.BuilderOptions;
import redempt.ordinate.builder.CommandBuilder;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.message.MessageFormatter;

/**
 * A spigot-specific {@link CommandBuilder}
 * @author Redempt
 */
public class SpigotCommandBuilder extends CommandBuilder<CommandSender, SpigotCommandBuilder> {
	
	public SpigotCommandBuilder(String[] names, CommandManager<CommandSender> manager, BuilderOptions<CommandSender> options) {
		super(names, manager, options, SpigotCommandBuilder::new);
	}
	
	private SpigotCommandManager getManager() {
		return (SpigotCommandManager) manager;
	}
	
	/**
	 * Sets the permission required to run the command
	 * @param permission The permission
	 * @return Itself
	 */
	public SpigotCommandBuilder permission(String permission) {
		MessageFormatter<CommandSender> msg = getManager().getMessages().getFormatter("noPermission");
		pipeline.addComponent(new PermissionComponent(permission, msg));
		return this;
	}
	
	/**
	 * Makes the command only executable by players
	 * @return Itself
	 */
	public SpigotCommandBuilder playerOnly() {
		MessageFormatter<CommandSender> msg = getManager().getMessages().getFormatter("playerOnly");
		pipeline.addComponent(new PlayerOnlyComponent(msg));
		return this;
	}
	
}
