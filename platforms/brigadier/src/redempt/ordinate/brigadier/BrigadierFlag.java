package redempt.ordinate.brigadier;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import redempt.ordinate.command.CommandBase;

import java.util.function.BiConsumer;

public class BrigadierFlag<C> {
	
	private ArgumentBuilder<C, ?> flag;
	private ArgumentBuilder<C, ?> arg;
	
	public BrigadierFlag(ArgumentBuilder<C, ?> flag, ArgumentBuilder<C, ?> arg) {
		this.flag = flag;
		this.arg = arg;
	}
	
	public boolean hasArg() {
		return arg != null;
	}
	
	public ArgumentBuilder<C, ?> getFlag() {
		return flag;
	}
	
	public ArgumentBuilder<C, ?> getArg() {
		return arg;
	}
	
	public CommandNode<C> build(boolean complete) {
		if (!hasArg()) {
			if (complete) {
				flag.executes(c -> 0);
			}
			CommandNode<C> built = flag.build();
			flag.executes(null);
			return built;
		}
		CommandNode<C> built = flag.build();
		if (complete) {
			arg.executes(c -> 0);
		}
		built.addChild(arg.build());
		arg.executes(null);
		return built;
	}
	
}
