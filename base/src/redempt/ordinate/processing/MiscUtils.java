package redempt.ordinate.processing;

public class MiscUtils {
	
	public static Boolean parseBoolean(String str) {
		switch (str) {
			case "true":
				return true;
			case "false":
				return false;
			default:
				return null;
		}
	}
	
}
