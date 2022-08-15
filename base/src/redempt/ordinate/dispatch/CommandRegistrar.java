package redempt.ordinate.dispatch;

import redempt.ordinate.command.CommandBase;

/**
 * Represents a service for registering and unregistering commands
 * @param <T> The sender type
 * @author Redempt
 */
public interface CommandRegistrar<T> {
	
	/**
	 * Registers a command
	 * @param command The command
	 */
	public void register(CommandBase<T> command);
	
	/**
	 * Unregisters a command
	 * @param command The command
	 */
	public void unregister(CommandBase<T> command);

}
