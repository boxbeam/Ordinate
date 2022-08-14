package redempt.ordinate.builder;

import redempt.ordinate.dispatch.CommandManager;

public interface CommandBuilderFactory<T, B extends CommandBuilder<T, B>> {
	
	public B create(String[] names, CommandManager<T> manager, BuilderOptions<T> options);
	
}
