package redempt.ordinate.spigot;

import org.bukkit.command.CommandSender;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.help.HelpEntry;
import redempt.ordinate.message.MessageFormatter;
import redempt.ordinate.message.MessageProvider;

public class SpigotHelpDisplayer implements HelpDisplayer<CommandSender> {
	
	private MessageFormatter<CommandSender> helpMessage;
	private String commandPrefix;
	
	public SpigotHelpDisplayer(String commandPrefix, MessageProvider<CommandSender> messages) {
		helpMessage = messages.getFormatter("helpFormat");
		this.commandPrefix = commandPrefix;
	}
	
	@Override
	public void display(CommandSender sender, HelpEntry entry) {
		String parentPrefix = entry.getParentPrefix();
		if (parentPrefix.length() != 0) {
			parentPrefix += " ";
		}
		String fullUsage = commandPrefix + parentPrefix + entry.getUsage();
		String description = entry.getDescription();
		helpMessage.format(sender, fullUsage, description).send(sender);
	}
	
}
