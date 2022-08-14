package redempt.ordinate.parser;

import redempt.ordinate.data.CommandContext;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.parser.metadata.MethodHook;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public class ReflectiveCommandDispatcher<T> implements CommandDispatcher<T> {

	private MethodHook hook;
	private List<Consumer<Object[]>> transformers;
	
	public ReflectiveCommandDispatcher(MethodHook hook) {
		this.hook = hook;
		initTransformers();
	}
	
	private void initTransformers() {
		transformers = new ArrayList<>();
		Class<?>[] types = hook.getMethod().getParameterTypes();
		for (int i = 0; i < types.length; i++) {
			Class<?> type = types[i];
			if (!type.isArray()) {
				continue;
			}
			transformers.add(createArrayTransformer(type.getComponentType(), i));
		}
	}
	
	private <T> Consumer<Object[]> createArrayTransformer(Class<?> componentType, int slot) {
		return array -> {
			Collection<T> collection = (Collection<T>) array[slot];
			Object arr = Array.newInstance(componentType, collection.size());
			int i = 0;
			for (T val : collection) {
				Array.set(arr, i, val);
				i++;
			}
			array[slot] = arr;
		};
	}
	
	@Override
	public void dispatch(CommandContext<T> context) {
		Object[] parsed = context.getAllParsed();
		transformers.forEach(c -> c.accept(parsed));
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
