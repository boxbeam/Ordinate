package redempt.ordinate.parser;

import redempt.ordinate.command.ArgType;
import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.context.ContextProvider;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.dispatch.DispatchComponent;
import redempt.ordinate.processing.CommandParsingPipeline;
import redempt.redlex.bnf.BNFParser;
import redempt.redlex.data.Token;
import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class CommandParser<T> {

	private static  Lexer lexer;

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
		String[] names = commandToken.getChildren()[0].joinLeaves("").split(",");
		CommandParsingPipeline<T> pipeline = new CommandParsingPipeline<>();
		if (argList != null) {
			parseArgumentTokens(argList.getChildren()).forEach(pipeline::addComponent);
		}
		Token bodyToken = commandToken.getChildren()[commandToken.getChildren().length - 1];
		Map<String, List<String>> tags = new LinkedHashMap<>();
		Command<T> cmd = new Command<>(names, pipeline);
		for (Token entry : bodyToken.getChildren()) {
			if (entry.getType().getName().equals("tag")) {
				String[] split = entry.getValue().split("\\s*=\\s*", 2);
				tags.computeIfAbsent(split[0], k -> new ArrayList<>()).add(split[1]);
				continue;
			}
			Command<T> child = parseCommand(entry);
			child.setParent(cmd);
			pipeline.addComponent(parseCommand(entry));
		}
		for (Map.Entry<String, List<String>> tag : tags.entrySet()) {
			TagProcessor<T> tagProcessor = options.getTagProcessor(tag.getKey());
			for (String tagValue : tag.getValue()) {
				cmd = tagProcessor.apply(cmd, tagValue);
			}
		}
		cmd.preparePipeline();
		return cmd;
	}

	private List<CommandComponent<T>> parseArgumentTokens(Token[] arguments) {
		List<CommandComponent<T>> list = new ArrayList<>();
		for (Token arg : arguments) {
			CommandComponent<T> component = options.getArgumentParser().parseArgument(arg.getValue(), options, manager.getComponentFactory());
			list.add(component);
		}
		return list;
	}

	private Token getArgListToken(Token commandToken) {
		Token second = commandToken.getChildren()[1];
		if (second.getType().getName().equals("paramList")) {
			return second;
		}
		return null;
	}

}
