package redempt.ordinate.parser;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.SubcommandLookupComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.parser.argument.ArgumentParser;
import redempt.ordinate.parser.metadata.CommandHook;
import redempt.ordinate.parser.metadata.MethodHook;
import redempt.ordinate.parser.metadata.ParserOptions;
import redempt.ordinate.processing.CommandParsingPipeline;
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
		for (Token commandToken : tokens) {
			commands.add(parseCommand(commandToken));
		}
		return new CommandCollection<>(commands, manager);
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
			return cmd;
		}));
		return this;
	}

	public CommandParser<T> setOptions(ParserOptions<T> parserOptions) {
		this.options = parserOptions;
		return this;
	}

	public CommandParser<T> addArgTypes(ArgType<T, ?>... argTypes) {
		for (ArgType<T, ?> argType : argTypes) {
			options.getArgumentTypes().put(argType.getName(), argType);
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

	private Command<T> parseCommand(Token commandToken) {
		Token argList = getArgListToken(commandToken);
		String[] names = commandToken.getChildren()[0].getValue().split(",");
		CommandParsingPipeline<T> pipeline = new CommandParsingPipeline<>();
		if (argList != null) {
			parseArgumentTokens(argList.getChildren(), pipeline);
		}
		Token bodyToken = commandToken.getChildren()[commandToken.getChildren().length - 1];
		Command<T> cmd = parseInternalEntries(new Command<>(manager.getCommandPrefix(), names, pipeline), bodyToken.getChildren());
		pipeline.getComponents().forEach(c -> c.setParent(cmd));
		cmd.preparePipeline();
		return cmd;
	}

	private Command<T> parseInternalEntries(Command<T> cmd, Token[] entries) {
		Map<String, List<String>> tags = new LinkedHashMap<>();
		List<Command<T>> subcommands = new ArrayList<>();
		for (Token entry : entries) {
			if (entry.getType().getName().equals("tag")) {
				String[] split = entry.getValue().split("\\s*=\\s*", 2);
				String tagName = split[0];
				String tagValue = split.length == 1 ? "" : split[1].trim();
				tags.computeIfAbsent(tagName, k -> new ArrayList<>()).add(tagValue);
				continue;
			}
			subcommands.add(parseCommand(entry));
		}
		SubcommandLookupComponent<T> lookup = manager.getComponentFactory().createLookupComponent(subcommands);
		lookup.setParent(cmd);
		cmd.getPipeline().addComponent(lookup);
		for (Map.Entry<String, List<String>> tag : tags.entrySet()) {
			TagProcessor<T> tagProcessor = options.getTagProcessor(tag.getKey());
			for (String tagValue : tag.getValue()) {
				cmd = tagProcessor.apply(cmd, tagValue);
			}
		}
		return cmd;
	}

	private void parseArgumentTokens(Token[] arguments, CommandParsingPipeline<T> pipeline) {
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
