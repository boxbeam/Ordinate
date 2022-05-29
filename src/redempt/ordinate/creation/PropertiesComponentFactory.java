package redempt.ordinate.creation;

import redempt.ordinate.component.BooleanFlagComponent;
import redempt.ordinate.component.argument.*;
import redempt.ordinate.constraint.ConstraintParser;
import redempt.ordinate.constraint.NumberConstraint;
import redempt.ordinate.context.ContextComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.dispatch.CommandDispatcher;
import redempt.ordinate.dispatch.DispatchComponent;
import redempt.ordinate.processing.MessageFormatter;

import java.util.*;
import java.util.function.Function;

public class PropertiesComponentFactory<T> implements ComponentFactory<T> {

	public static Properties getDefaultMessages() {
		Properties props = new Properties();
		props.setProperty("missingArgument", "Missing value for argument: %1");
		props.setProperty("invalidArgumentValue", "Invalid value for argument %1: %2");
		props.setProperty("executionFailed", "Command execution failed due to an unexpected error. Please report this to an administrator.");
		props.setProperty("tooManyArguments", "Too many arguments supplied: Extra %1 argument(s) provided");
		props.setProperty("numberOutsideRange", "Number %1 outside range: %2");
		return props;
	}

	private MessageFormatter<T> formatter = (sender, msg) -> msg[0];
	private Map<String, MessageFormatter<T>> messages = new HashMap<>();

	public PropertiesComponentFactory(Properties properties) {
		for (Object key : properties.keySet()) {
			String stringKey = key.toString();
			messages.put(stringKey, createFormatter(properties.getProperty(stringKey)));
		}
	}
	
	public MessageFormatter<T> createFormatter(String message) {
		return (sender, placeholders) -> {
			String msg = message;
			for (int i = 0; i < placeholders.length; i++) {
				msg = msg.replace("%" + (i + 1), placeholders[i]);
			}
			msg = formatter.apply(sender, msg);
			return msg;
		};
	}

	private MessageFormatter<T> getMessage(String key) {
		MessageFormatter<T> message = getMessage(key);
		if (message == null) {
			throw new IllegalArgumentException("No message with key " + key);
		}
		return message;
	}
	
	@Override
	public <V> ArgumentComponent<T, V> createArgument(ArgType<T, V> type, String name) {
		return new ArgumentComponent<>(name, type, getMessage("missingArgument"), getMessage("invalidArgumentValue"));
	}

	@Override
	public <V> OptionalArgumentComponent<T, V> createOptionalArgument(ArgType<T, V> type, ContextProvider<T, V> defaultValue, String name) {
		return new OptionalArgumentComponent<>(name, type, defaultValue, getMessage("invalidArgument"));
	}

	@Override
	public <V> ConsumingArgumentComponent<T, V> createConsumingArgument(ArgType<T, V> type, boolean optional, ContextProvider<T, V> defaultValue, String name) {
		return new ConsumingArgumentComponent<>(name, type, optional, defaultValue, getMessage("missingArgument"), getMessage("invalidArgumentValue"));
	}

	@Override
	public <V> VariableLengthArgumentComponent<T, V> createVariableLengthArgument(ArgType<T, V> type, boolean optional, String name) {
		return new VariableLengthArgumentComponent<>(name, type, optional, getMessage("missingArgument"), getMessage("invalidArgumentValue"));
	}

	@Override
	public BooleanFlagComponent<T> createBooleanFlag(String... names) {
		String primaryName = names[0];
		Set<String> allNames = new HashSet<>();
		Collections.addAll(allNames, names);
		return new BooleanFlagComponent<>(primaryName, allNames);
	}

	@Override
	public <V> ContextComponent<T, V> createContext(ContextProvider<T, V> provider, String name, MessageFormatter<T> error) {
		return new ContextComponent<>(name, provider, error);
	}

	@Override
	public DispatchComponent<T> createDispatch(CommandDispatcher<T> dispatcher) {
		return new DispatchComponent<>(dispatcher, getMessage("executionFailed"), getMessage("tooManyArguments"));
	}

	@Override
	public <V extends Number & Comparable<V>> ConstraintParser<T, V> createNumberConstraintParser(Function<String, V> parseNumber) {
		return NumberConstraint.createParser(parseNumber, getMessage("numberOutsideRange"));
	}

}
