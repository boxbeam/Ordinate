package redempt.ordinate.test;

import redempt.ordinate.builder.BuilderOptions;
import redempt.ordinate.builder.CommandBuilder;
import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.creation.DefaultComponentFactory;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.dispatch.CommandRegistrar;
import redempt.ordinate.help.HelpDisplayer;
import redempt.ordinate.message.MessageProvider;
import redempt.ordinate.message.PropertiesMessageProvider;
import redempt.ordinate.parser.CommandParser;
import redempt.ordinate.parser.metadata.ParserOptions;

public class TestCommandManager implements CommandManager<Void> {
	
	private ComponentFactory<Void> factory;
	
	public TestCommandManager() {
		MessageProvider<Void> messages = new PropertiesMessageProvider<>(PropertiesMessageProvider.getDefaultMessages(), (c, s) -> {});
		factory = new DefaultComponentFactory<>(messages);
	}
	
	@Override
	public CommandRegistrar<Void> getRegistrar() {
		return null;
	}
	
	@Override
	public HelpDisplayer<Void> getHelpDisplayer() {
		return (a, b) -> {};
	}
	
	@Override
	public ComponentFactory<Void> getComponentFactory() {
		return factory;
	}
	
	@Override
	public CommandParser<Void> getParser() {
		return new CommandParser<>(ParserOptions.getDefaults(factory), this);
	}
	
	@Override
	public CommandBuilder<Void, ?> builder(String... names) {
		return null;
	}
	
	@Override
	public String getCommandPrefix() {
		return "";
	}
	
}
