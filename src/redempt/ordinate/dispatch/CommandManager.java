package redempt.ordinate.dispatch;

import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.parser.CommandParser;

public interface CommandManager<T> {

	public CommandRegistrar<T> getRegistrar();
	public HelpDisplayer<T> getHelpDisplayer();
	public MessageDispatcher<T> getMessageDispatcher();
	public ComponentFactory<T> getComponentFactory();
	public CommandParser<T> getCommandParser();
	public void setComponentFactory(ComponentFactory<T> componentFactory);
	public String getCommandPrefix();

}
