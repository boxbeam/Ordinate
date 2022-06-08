package redempt.ordinate.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CompletionResult<T> {

	private CommandResult<T> error;
	private List<String> completions;

	public CompletionResult(String beginning, CommandResult<T> result, Collection<String> completions) {
		this.error = result;
		beginning = beginning.toLowerCase();
		this.completions = new ArrayList<>();
		for (String completion : completions) {
			if (!completion.regionMatches(true, 0, beginning, 0, beginning.length())) {
				continue;
			}
			if (completion.contains(" ")) {
				completion = '"' + completion.replaceAll("([\"\\\\])", "\\\\$1") + '"';
			}
			this.completions.add(completion);
		}
	}

	public CommandResult<T> getError() {
		return error;
	}

	public List<String> getCompletions() {
		return completions;
	}

}
