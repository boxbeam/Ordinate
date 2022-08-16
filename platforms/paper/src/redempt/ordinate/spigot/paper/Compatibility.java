package redempt.ordinate.spigot.paper;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;

class Compatibility {
	
	private static boolean initialized = false;
	private static boolean supportsBrigadier = false;
	private static boolean supportsRawCommands = false;
	
	private static void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;
		try {
			Class<?> clazz = Class.forName("com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent");
			supportsBrigadier = true;
			clazz.getMethod("setRawCommand", boolean.class);
			supportsRawCommands = true;
		} catch (ClassNotFoundException | NoSuchMethodException e) {
		}
	}
	
	public static boolean supportsBrigadier() {
		initialize();
		return supportsBrigadier;
	}
	
	public static boolean supportsRawCommands() {
		initialize();
		return supportsRawCommands;
	}
	
}
