package redempt.ordinate.dispatch;

import redempt.ordinate.builder.CommandBuilder;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.parser.CommandParser;

public interface CommandManager<T> {

	public CommandRegistrar<T> getRegistrar();
	public HelpDisplayer<T> getHelpDisplayer();
	public ComponentFactory<T> getComponentFactory();
	public CommandParser<T> getParser();
	public CommandBuilder<T, ?> builder(String... names);
	public String getCommandPrefix();

}
