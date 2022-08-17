package redempt.ordinate.spigot.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import redempt.ordinate.command.CommandBase;
import redempt.ordinate.dispatch.CommandRegistrar;
import redempt.ordinate.spigot.SpigotCommandRegistrar;

import java.util.*;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class PaperCommandRegistrar<S extends BukkitBrigadierCommandSource> implements CommandRegistrar<CommandSender> {

	private Plugin plugin;
	private Map<Command, CommandBase<CommandSender>> commands = new HashMap<>();
	private PaperCommandManager<S> manager;
	
	PaperCommandRegistrar(Plugin plugin, PaperCommandManager<S> manager) {
		this.plugin = plugin;
		this.manager = manager;
		if (Compatibility.supportsBrigadier()) {
			Bukkit.getPluginManager().registerEvents(new BrigadierListener(), plugin);
		}
	}
	
	@Override
	public void register(CommandBase<CommandSender> command) {
		Command cmd = SpigotCommandRegistrar.createSpigotCommand(command);
		commands.put(cmd, command);
		Bukkit.getCommandMap().register(manager.getFallbackPrefix(), cmd);
	}
	
	@Override
	public void unregister(CommandBase<CommandSender> command) {
	
	}
	
	class BrigadierListener implements Listener {
		
		@EventHandler
		public void onCommandRegister(CommandRegisteredEvent<S> e) {
			CommandBase<CommandSender> base = commands.get(e.getCommand());
			if (base == null) {
				return;
			}
			if (Compatibility.supportsRawCommands()) {
//				e.setRawCommand(true);
			}
			CommandNode<S> node = manager.getBrigadierConverter().convertToBrigadier(base);
			LiteralCommandNode<S> literal = e.getLiteral();
			literal.getChildren().remove(literal.getChild("args"));
			node.getChildren().forEach(command -> {
				command.getChildren().forEach(literal::addChild);
			});
			e.setLiteral(literal);
		}
		
	}
	
}
