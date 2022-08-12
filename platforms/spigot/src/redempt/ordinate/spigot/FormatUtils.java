package redempt.ordinate.spigot;

import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class FormatUtils {
	
	private static Set<Character> colorChars = "4c6e2ab319d5f780rlonmk".chars().mapToObj(i -> (char) i).collect(Collectors.toSet());
	
	/**
	 * Shorthand for {@link ChatColor#translateAlternateColorCodes(char, String)} with the option to format hex color codes
	 * @param input The input string
	 * @param hex Whether to translate hex color codes for 1.16+ (format: {@literal &#FF0000})
	 * @return The colored string, replacing color codes using ampersands with proper codes
	 */
	public static String color(String input, boolean hex) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (i + 1 >= input.length()) {
				builder.append(c);
				continue;
			}
			char n = input.charAt(i + 1);
			if (c == '\\' && (n == '&' || n == '\\')) {
				i++;
				builder.append(n);
				continue;
			}
			if (c != '&') {
				builder.append(c);
				continue;
			}
			if (colorChars.contains(n)) {
				builder.append(ChatColor.COLOR_CHAR);
				continue;
			}
			if (hex && n == '#' && i + 7 <= input.length()) {
				String hexCode = input.substring(i + 2, i + 8).toUpperCase(Locale.ROOT);
				if (hexCode.chars().allMatch(ch -> (ch <= '9' && ch >= '0') || (ch <= 'F' && ch >= 'A'))) {
					hexCode = Arrays.stream(hexCode.split("")).map(s -> ChatColor.COLOR_CHAR + s).collect(Collectors.joining());
					builder.append(ChatColor.COLOR_CHAR).append("x").append(hexCode);
					i += 7;
					continue;
				}
			}
			builder.append(c);
		}
		return builder.toString();
	}
	
}
