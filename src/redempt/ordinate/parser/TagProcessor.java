package redempt.ordinate.parser;

import redempt.ordinate.command.Command;
import redempt.ordinate.data.Named;

import java.util.function.BiFunction;

public interface TagProcessor<T> extends Named {

	public static <T> TagProcessor<T> create(String name, BiFunction<Command<T>, String, Command<T>> processor) {
		return new TagProcessor<T>() {
			@Override
			public Command<T> apply(Command<T> cmd, String value) {
				return processor.apply(cmd, value);
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}

	public Command<T> apply(Command<T> cmd, String value);

}
