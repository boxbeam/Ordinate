package redempt.ordinate.parser;

import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.creation.ComponentFactory;

public interface ArgumentParser<T> {

	public <V> CommandComponent<T> parseArgument(String argument, ParserOptions<T> options, ComponentFactory<T> componentFactory);

}
