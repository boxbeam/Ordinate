package redempt.ordinate.parser.argument;

import redempt.ordinate.creation.ComponentFactory;
import redempt.ordinate.parser.metadata.ParserOptions;
import redempt.ordinate.processing.CommandParsingPipeline;
import redempt.redlex.data.Token;

public interface ArgumentParser<T> {

	public <V> void parseArgument(Token argument, ParserOptions<T> options, ComponentFactory<T> componentFactory, CommandParsingPipeline<T> pipeline);

}
