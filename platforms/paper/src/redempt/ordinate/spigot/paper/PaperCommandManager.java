package redempt.ordinate.spigot.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import redempt.ordinate.brigadier.BrigadierCommandConverter;
import redempt.ordinate.command.CommandBase;
import redempt.ordinate.data.CompletionResult;
import redempt.ordinate.dispatch.CommandRegistrar;
import redempt.ordinate.message.MessageProvider;
import redempt.ordinate.message.PropertiesMessageProvider;
import redempt.ordinate.spigot.SpigotCommandManager;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class PaperCommandManager<S extends BukkitBrigadierCommandSource> extends SpigotCommandManager {
	
	public static <S extends BukkitBrigadierCommandSource> PaperCommandManager<S> getPaperInstance(Plugin plugin, String fallbackPrefix, Properties messages) {
		return new PaperCommandManager<>(plugin, fallbackPrefix, new PropertiesMessageProvider<>(messages, CommandSender::sendMessage));
	}
	
	public static <S extends BukkitBrigadierCommandSource> PaperCommandManager<S> getPaperInstance(Plugin plugin, String fallbackPrefix) {
		return getPaperInstance(plugin, fallbackPrefix, SpigotCommandManager.getDefaultMessages());
	}
	
	public static <S extends BukkitBrigadierCommandSource> PaperCommandManager<S> getPaperInstance(Plugin plugin) {
		return getPaperInstance(plugin, plugin.getName().toLowerCase(), SpigotCommandManager.getDefaultMessages());
	}
	
	private BrigadierCommandConverter<CommandSender, S> converter;
	private PaperCommandRegistrar<S> registrar;
	
	protected PaperCommandManager(Plugin plugin, String fallbackPrefix, MessageProvider<CommandSender> messages) {
		super(plugin, fallbackPrefix, messages);
		if (Compatibility.supportsBrigadier()) {
			converter = new BrigadierCommandConverter<>();
			converter.setNodeMutator((cmd, node) -> {
				register((CommandBase<CommandSender>) cmd, node);
			});
		}
		registrar = new PaperCommandRegistrar<>(plugin, this);
	}
	
	private void register(CommandBase<CommandSender> base, ArgumentBuilder<S, ?> arg) {
		arg.executes(ctx -> {
			String contents = ctx.getInput().split(" ", 2)[1];
			CommandSender sender = ctx.getSource().getBukkitSender();
			base.execute(sender, contents);
			return 0;
		});
		if (!(arg instanceof RequiredArgumentBuilder)) {
			return;
		}
		RequiredArgumentBuilder<S, ?> req = (RequiredArgumentBuilder<S, ?>) arg;
		req.suggests((ctx, builder) -> CompletableFuture.supplyAsync(() -> {
			String contents = ctx.getInput().split(" ", 2)[1];
			CommandSender sender = ctx.getSource().getBukkitSender();
			CompletionResult<?> result = base.getCompletions(sender, contents);
			result.getCompletions().forEach(builder::suggest);
			return builder.build();
		}));
	}
	
	protected BrigadierCommandConverter<CommandSender, S> getBrigadierConverter() {
		return converter;
	}
	
	@Override
	public CommandRegistrar<CommandSender> getRegistrar() {
		return registrar;
	}
	
}
