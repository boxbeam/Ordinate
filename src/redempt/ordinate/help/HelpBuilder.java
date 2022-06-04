package redempt.ordinate.help;

import java.util.ArrayList;
import java.util.List;

public class HelpBuilder {

	private List<HelpComponent> components = new ArrayList<>();

	public void addHelp(HelpComponent component) {
		components.add(component);
	}

}
