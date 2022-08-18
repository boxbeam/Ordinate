package redempt.ordinate.test;

import redempt.ordinate.command.CommandBase;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.data.CommandResult;
import redempt.ordinate.data.CompletionResult;
import redempt.ordinate.parser.CommandParser;
import redempt.ordinate.parser.TagProcessor;

import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTester {
	
	private Object[] output;
	private Map<String, CommandBase<Void>> commands = new HashMap<>();
	
	public CommandTester(InputStream stream) {
		TestCommandManager manager = new TestCommandManager();
		CommandParser<Void> parser = manager.getParser();
		parser.addTagProcessors(TagProcessor.create("hook", (cmd, value) -> {
			cmd.getPipeline().addComponent(manager.getComponentFactory().createDispatch(ctx -> {
				Object[] arr = ctx.getAllParsed();
				output = Arrays.copyOfRange(arr, 1, arr.length);
			}));
		}));
		parser.addContextProviders(ContextProvider.create("test", "", ctx -> "test"));
		parser.parse(stream).getCommands().forEach(command -> command.getNames().forEach(name -> commands.put(name, command)));
	}
	
	private CommandResult<Void> run(String command) {
		String[] split = command.split(" ", 2);
		String name = split[0];
		String args = split.length == 1 ? "" : split[1];
		CommandBase<Void> commandBase = commands.get(name);
		assertNotNull(commandBase);
		return commandBase.execute(null, args);
	}
	
	public void expect(String command, Object... output) {
		this.output = null;
		CommandResult<Void> result = run(command);
		if (!result.isSuccess()) {
			throw new IllegalStateException(result.getError().toString());
		}
		assertArrayEquals(output, this.output);
	}
	
	public void expectFailure(String command) {
		assertFalse(run(command).isSuccess());
	}
	
	public void expectCompletions(String command, String... completions) {
		String[] split = command.split(" ", 2);
		String name = split[0];
		String args = split.length == 1 ? "" : split[1];
		CommandBase<Void> commandBase = commands.get(name);
		assertNotNull(commandBase);
		CompletionResult<Void> result = commandBase.getCompletions(null, args);
		Set<String> actualCompletions = new HashSet<>(result.getCompletions());
		Set<String> expectedCompletions = new HashSet<>();
		Collections.addAll(expectedCompletions, completions);
		assertEquals(expectedCompletions, actualCompletions);
	}

}
