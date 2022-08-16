package redempt.ordinate.spigot.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import redempt.ordinate.brigadier.BrigadierCommandConverter;
import redempt.ordinate.message.MessageProvider;
import redempt.ordinate.spigot.SpigotCommandManager;

public class PaperCommandManager<S extends BukkitBrigadierCommandSource> extends SpigotCommandManager {
	
	private BrigadierCommandConverter<CommandSender, S> converter;
	
	protected PaperCommandManager(Plugin plugin, String fallbackPrefix, MessageProvider<CommandSender> messages) {
		super(plugin, fallbackPrefix, messages);
		if (Compatibility.supportsBrigadier()) {
			converter = new BrigadierCommandConverter<>();
		}
	}
	
	protected BrigadierCommandConverter<CommandSender, S> getBrigadierConverter() {
		return converter;
	}
	
}
