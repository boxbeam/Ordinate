package redempt.ordinate.processing;

import redempt.ordinate.data.Argument;
import redempt.ordinate.data.SplittableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArgumentSplitter {

	public static SplittableList<Argument> split(String[] command, boolean forCompletions) {
		return split(String.join(" ", command), forCompletions);
	}

	public static SplittableList<Argument> split(String command, boolean forCompletions) {
		List<Argument> args = new ArrayList<>();
		boolean quoted = false;
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < command.length(); i++) {
			char c = command.charAt(i);
			if (c == '\\' && i + 1 < command.length() && command.charAt(i + 1) == '"') {
				i++;
				buffer.append('"');
				continue;
			}
			if (c == '"' && (quoted || i == 0 || command.charAt(i - 1) == ' ')) {
				quoted = !quoted;
				if (!quoted && buffer.length() > 0) {
					args.add(new Argument(buffer.toString(), true));
					buffer.setLength(0);
				}
				continue;
			}
			if (c == ' ') {
				if (!quoted && buffer.length() > 0) {
					args.add(new Argument(buffer.toString(), false));
					buffer.setLength(0);
				}
				continue;
			}
			buffer.append(c);
		}
		if (buffer.length() > 0 || forCompletions) {
			String last = buffer.toString();
			last = (quoted ? "\"" : "") + last;
			String[] split = last.split(" ", forCompletions ? -1 : 0);
			for (String arg : split) {
				args.add(new Argument(arg, false));
			}
		}
		Argument[] array = args.toArray(new Argument[0]);
		return new SplittableList<>(array);
	}

}
