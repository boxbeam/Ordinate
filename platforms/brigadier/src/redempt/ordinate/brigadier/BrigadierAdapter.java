package redempt.ordinate.brigadier;

import com.mojang.brigadier.tree.CommandNode;
import redempt.ordinate.component.abstracts.CommandComponent;

import java.util.List;

public interface BrigadierAdapter<T, C extends CommandComponent<T>> {

	public void convert(C component, BrigadierBuilder<?> builder);
	
}
