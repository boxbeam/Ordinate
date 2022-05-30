package redempt.ordinate.parser;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.parser.metadata.MethodHook;

import java.lang.reflect.InvocationTargetException;
import java.util.StringJoiner;

public class ReflectiveCommandDispatcher<T> implements CommandDispatcher<T> {

	private MethodHook hook;

	public ReflectiveCommandDispatcher(MethodHook hook) {
		this.hook = hook;
	}

	@Override
	public void dispatch(CommandContext<T> context) {
		Object[] parsed = context.getAllParsed();
		try {
			hook.getMethod().invoke(hook.getTarget(), parsed);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			StringJoiner joiner = new StringJoiner(", ", "[", "]");
			for (Object obj : parsed) {
				joiner.add(obj == null ? "null" : obj.getClass().getName());
			}
			throw new IllegalStateException("Could not pass arguments to method hook " + hook.getMethod().getName() + ", types passed: " + joiner);
		}
	}

}
