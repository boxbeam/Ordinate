package redempt.ordinate.component.abstracts;

import redempt.ordinate.command.Command;

import java.util.Collection;

public interface CommandParent<T> {

	public Collection<Command<T>> getSubcommands();

}
