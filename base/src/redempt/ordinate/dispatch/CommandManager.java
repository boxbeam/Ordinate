package redempt.ordinate.dispatch;

import redempt.ordinate.builder.CommandBuilder;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.parser.CommandParser;

/**
 * A centralized handler for all command creation and registration purposes
 * @param <T> The sender type
 * @author Redempt
 */
public interface CommandManager<T> {
	
	/**
	 * @return The registrar which can be used to register and unregister commands
	 */
	public CommandRegistrar<T> getRegistrar();
	
	/**
	 * @return The help displayer, used internally to display help messages
	 */
	public HelpDisplayer<T> getHelpDisplayer();
	
	/**
	 * @return The component factory, used internally to initialize command components
	 */
	public ComponentFactory<T> getComponentFactory();
	
	/**
	 * @return A command parser instance, used to parse command files and register the parsed commands
	 */
	public CommandParser<T> getParser();
	
	/**
	 * Creates a new command builder
	 * @param names The names of the command
	 * @return A new command builder
	 */
	public CommandBuilder<T, ?> builder(String... names);
	
	/**
	 * @return The prefix that appears before all commands
	 */
	public String getCommandPrefix();

}
