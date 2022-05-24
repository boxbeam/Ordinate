package redempt.ordinate.parsing;

import redempt.ordinate.data.Argument;
import redempt.ordinate.data.SplittableList;

import java.util.ArrayList;
import java.util.List;

public class ArgumentSplitter {

	public static SplittableList<Argument> split(String[] command) {
		return split(String.join(" ", command));
	}

	public static SplittableList<Argument> split(String command) {
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
			if (c == '"') {
				quoted = !quoted;
				if (!quoted) {
					args.add(new Argument(buffer.toString(), true));
					buffer.setLength(0);
				}
				continue;
			}
			if (c == ' ' && !quoted && buffer.length() > 0) {
				args.add(new Argument(buffer.toString(), false));
				buffer.setLength(0);
				continue;
			}
			buffer.append(c);
		}
		if (buffer.length() > 0) {
			String last = buffer.toString();
			last = (quoted ? "\"" : "") + last;
			args.add(new Argument(last, false));
		}
		Argument[] array = args.toArray(new Argument[0]);
		return new SplittableList<>(array);
	}

}
