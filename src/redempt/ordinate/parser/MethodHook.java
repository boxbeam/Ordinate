package redempt.ordinate.parser;

import java.lang.reflect.Method;

public class MethodHook {

	private Method method;
	private Object target;

	public MethodHook(Method method, Object target) {
		this.method = method;
		this.target = target;
	}

	public Method getMethod() {
		return method;
	}

	public Object getTarget() {
		return target;
	}

}
