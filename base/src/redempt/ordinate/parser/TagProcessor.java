package redempt.ordinate.parser;

import redempt.ordinate.command.Command;
import redempt.ordinate.data.Named;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface TagProcessor<T> extends Named {

	public static <T> TagProcessor<T> create(String name, BiConsumer<Command<T>, String> processor) {
		return new TagProcessor<T>() {
			@Override
			public void apply(Command<T> cmd, String value) {
				processor.accept(cmd, value);
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}

	public void apply(Command<T> cmd, String value);

}
