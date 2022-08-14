package redempt.ordinate.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.UnaryOperator;

public class PropertiesMessageProvider<T> implements MessageProvider<T> {
	
	public static Properties getDefaultMessages() {
		Properties props = new Properties();
		props.setProperty("missingArgument", "Missing required argument: %1");
		props.setProperty("invalidArgumentValue", "Invalid value for argument %1: %2");
		props.setProperty("executionFailed", "Command execution failed due to an unexpected error. Please report this to an administrator.");
		props.setProperty("tooManyArguments", "Too many arguments: Extra %1 argument(s) provided");
		props.setProperty("numberOutsideRange", "Number %1 outside range: %2");
		props.setProperty("contextError", "%1");
		props.setProperty("constraintError", "Constraint failed for %1: %2");
		props.setProperty("invalidSubcommand", "Invalid subcommand: %1");
		return props;
	}
	
	private Map<String, MessageFormatter<T>> formatters = new HashMap<>();
	
	public PropertiesMessageProvider(Properties properties, BiConsumer<T, String> send, UnaryOperator<String> format) {
		properties.keySet().forEach(k -> {
			String key = (String) k;
			String message = format.apply(properties.getProperty(key));
			MessageFormatter<T> formatter = (sender, context) -> {
				String msg = message;
				for (int i = 0; i < context.length; i++) {
					msg = msg.replace("%" + (i + 1), context[i]);
				}
				return new StringMessage<>(msg, send);
			};
			formatters.put(key, formatter);
		});
	}
	
	public PropertiesMessageProvider(Properties properties, BiConsumer<T, String> send) {
		this(properties, send, UnaryOperator.identity());
	}
	
	@Override
	public MessageFormatter<T> getFormatter(String name) {
		MessageFormatter<T> formatter = formatters.get(name);
		if (formatter == null) {
			throw new IllegalArgumentException("Invalid message name: " + name);
		}
		return formatter;
	}
	
}
