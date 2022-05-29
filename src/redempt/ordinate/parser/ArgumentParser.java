package redempt.ordinate.parser;

import redempt.ordinate.component.abstracts.CommandComponent;

public interface ArgumentParser<T> {

	public CommandComponent<T> parseArgument(String argument);

}
