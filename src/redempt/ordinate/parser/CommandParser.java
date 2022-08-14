package redempt.ordinate.parser;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.HelpSubcommandComponent;
import redempt.ordinate.component.SubcommandLookupComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.parser.argument.ArgumentParser;
import redempt.ordinate.parser.metadata.CommandHook;
import redempt.ordinate.parser.metadata.MethodHook;
import redempt.ordinate.parser.metadata.ParserOptions;
import redempt.ordinate.processing.CommandPipeline;
import redempt.redlex.bnf.BNFParser;
import redempt.redlex.data.Token;
import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;
import redempt.redlex.processing.TraversalOrder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class CommandParser<T> {

	private static Lexer lexer;

	static {
		InputStream inp = CommandParser.class.getClassLoader().getResourceAsStream("ordn.bnf");
		lexer = BNFParser.createLexer(inp);
		lexer.setUnnamedRule(CullStrategy.LIFT_CHILDREN);
		lexer.setRetainEmpty(false);
		lexer.setRetainStringLiterals(false);
		lexer.setRuleByName(CullStrategy.DELETE_ALL, "sep", "break", "comment");
		lexer.setRuleByName(CullStrategy.LIFT_CHILDREN, "param", "entry");
	}

	private ParserOptions<T> options;
	private CommandManager<T> manager;

	public CommandParser(ParserOptions<T> options, CommandManager<T> manager) {
		this.options = options;
		this.manager = manager;
	}

	public CommandCollection<T> parse(String input) {
		Token root = lexer.tokenize(input);
		List<Token> tokens = root.allByName(TraversalOrder.SHALLOW, "command");
		List<Command<T>> commands = new ArrayList<>();
		Map<Command<T>, List<String>> tags = new HashMap<>();
		for (Token commandToken : tokens) {
			commands.add(parseCommand(commandToken, tags));
		}
		tags.forEach((cmd, tagList) -> tagList.forEach(tag -> {
			String[] split = tag.split("=", 2);
			String name = split[0].trim();
			String value = split.length == 1 ? "" : split[1].trim();
			options.getTagProcessor(name).apply(cmd, value);
		}));
		commands.forEach(this::prepare);
		return new CommandCollection<>(commands, manager);
	}
	
	private void prepare(Command<T> cmd) {
		Queue<Command<T>> queue = new ArrayDeque<>();
		queue.add(cmd);
		while (!queue.isEmpty()) {
			Command<T> next = queue.poll();
			next.preparePipeline(manager);
			queue.addAll(next.getSubcommands());
		}
	}

	public CommandCollection<T> parse(InputStream input) {
		String contents = new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"));
		return parse(contents);
	}

	public CommandParser<T> setHookTargets(Object... hookTargets) {
		Map<String, MethodHook> hooks = new HashMap<>();
		for (Object obj : hookTargets) {
			for (Method method : obj.getClass().getDeclaredMethods()) {
				if (!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
					continue;
				}
				CommandHook hook = method.getAnnotation(CommandHook.class);
				if (hook == null) {
					continue;
				}
				hooks.put(hook.value(), new MethodHook(method, obj));
			}
		}
		options.getTagProcessors().put("hook", TagProcessor.create("hook", (cmd, str) -> {
			MethodHook hook = hooks.get(str);
			if (hook == null) {
				throw new IllegalStateException("No method hook found for command with hook name " + str);
			}
			cmd.getPipeline().addComponent(manager.getComponentFactory().createDispatch(new ReflectiveCommandDispatcher<>(hook)));
		}));
		return this;
	}

	public CommandParser<T> setOptions(ParserOptions<T> parserOptions) {
		this.options = parserOptions;
		return this;
	}

	public CommandParser<T> addArgTypes(ArgType<?, ?>... argTypes) {
		for (ArgType<?, ?> argType : argTypes) {
			options.getArgumentTypes().put(argType.getName(), (ArgType<T, ?>) argType);
		}
		return this;
	}

	public CommandParser<T> addContextProviders(ContextProvider<T, ?>... contextProviders) {
		for (ContextProvider<T, ?> contextProvider : contextProviders) {
			options.getContextProviders().put(contextProvider.getName(), contextProvider);
		}
		return this;
	}

	public CommandParser<T> addTagProcessors(TagProcessor<T>... tagProcessors) {
		for (TagProcessor<T> tagProcessor : tagProcessors) {
			options.getTagProcessors().put(tagProcessor.getName(), tagProcessor);
		}
		return this;
	}

	public CommandParser<T> setArgumentParser(ArgumentParser<T> argumentParser) {
		options.setArgumentParser(argumentParser);
		return this;
	}

	public ParserOptions<T> getOptions() {
		return options;
	}
	
	public CommandParser<T> setHelpSubcommandName(String helpSubcommandName) {
		options.setHelpSubcommandName(helpSubcommandName);
		return this;
	}

	private Command<T> parseCommand(Token commandToken, Map<Command<T>, List<String>> tags) {
		Token argList = getArgListToken(commandToken);
		String[] names = commandToken.getChildren()[0].getValue().split(",");
		CommandPipeline<T> pipeline = new CommandPipeline<>();
		if (options.getHelpSubcommandName() != null) {
			pipeline.addComponent(new HelpSubcommandComponent<>(options.getHelpSubcommandName()));
		}
		if (argList != null) {
			parseArgumentTokens(argList.getChildren(), pipeline);
		}
		Token bodyToken = commandToken.getChildren()[commandToken.getChildren().length - 1];
		Command<T> cmd = parseInternalEntries(new Command<>(names, pipeline), bodyToken.getChildren(), tags);
		return cmd;
	}

	private Command<T> parseInternalEntries(Command<T> cmd, Token[] entries, Map<Command<T>, List<String>> tags) {
		for (Token entry : entries) {
			if (entry.getType().getName().equals("tag")) {
				tags.computeIfAbsent(cmd, k -> new ArrayList<>()).add(entry.getValue());
				continue;
			}
			Command<T> subcommand = parseCommand(entry, tags);
			subcommand.setParent(cmd);
			cmd.getPipeline().addComponent(subcommand);
		}
		return cmd;
	}

	private void parseArgumentTokens(Token[] arguments, CommandPipeline<T> pipeline) {
		for (Token arg : arguments) {
			options.getArgumentParser().parseArgument(arg, options, manager.getComponentFactory(), pipeline);
		}
	}

	private Token getArgListToken(Token commandToken) {
		Token second = commandToken.getChildren()[1];
		if (second.getType().getName().equals("paramList")) {
			return second;
		}
		return null;
	}

}
