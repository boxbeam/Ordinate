package redempt.ordinate.brigadier;

import com.mojang.brigadier.builder.ArgumentBuilder;
import redempt.ordinate.command.CommandBase;

import java.util.function.BiConsumer;

public interface NodeMutator<C> extends BiConsumer<CommandBase<?>, ArgumentBuilder<C, ?>> {
}
