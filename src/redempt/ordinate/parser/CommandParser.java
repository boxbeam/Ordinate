package redempt.ordinate.parser;

import redempt.ordinate.dispatch.CommandManager;
import redempt.redlex.bnf.BNFParser;
import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;

import java.io.InputStream;

public class CommandParser<T> {

	private static  Lexer lexer;

	static {
		InputStream inp = CommandParser.class.getClassLoader().getResourceAsStream("ordn.bnf");
		lexer = BNFParser.createLexer(inp);
		lexer.setUnnamedRule(CullStrategy.LIFT_CHILDREN);
		lexer.setRetainEmpty(false);
		lexer.setRetainStringLiterals(false);
		lexer.setRuleByName(CullStrategy.DELETE_ALL, "sep", "break");
	}

	private ParserOptions<T> options;
	private CommandManager<T> manager;

}
