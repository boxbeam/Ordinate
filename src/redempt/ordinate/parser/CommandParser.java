package redempt.ordinate.parser;

import redempt.ordinate.command.Command;
import redempt.ordinate.component.abstracts.CommandComponent;
import redempt.ordinate.dispatch.CommandManager;
import redempt.ordinate.processing.CommandParsingPipeline;
import redempt.redlex.bnf.BNFParser;
import redempt.redlex.data.Token;
import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		pipeline.prepare();
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
