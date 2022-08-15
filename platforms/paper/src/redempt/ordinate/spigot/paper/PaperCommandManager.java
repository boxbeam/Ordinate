package redempt.ordinate.spigot.paper;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import redempt.ordinate.message.MessageProvider;
import redempt.ordinate.spigot.SpigotCommandManager;

public class PaperCommandManager extends SpigotCommandManager {
	
	
	protected PaperCommandManager(Plugin plugin, String fallbackPrefix, MessageProvider<CommandSender> messages) {
		super(plugin, fallbackPrefix, messages);
	}
	
}
