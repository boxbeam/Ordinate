package redempt.ordinate.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class OrdinateTest {

	private static CommandTester tester;
	
	@BeforeAll
	public static void setup() {
		tester = new CommandTester(OrdinateTest.class.getResourceAsStream("/ordinate-test.ordn"));
	}
	
	@Test
	public void noArgTest() {
		tester.expect("noArg");
	}
	
	@Test
	public void oneStringArgTest() {
		tester.expect("stringArg abc", "abc");
		tester.expect("stringArg \"a b c d e f\"", "a b c d e f");
		tester.expectFailure("stringArg");
	}
	
	@Test
	public void twoStringArgTest() {
		tester.expect("stringArg a b", "a", "b");
		tester.expect("stringArg \"a b\" c", "a b", "c");
		tester.expectFailure("stringArg a b c");
	}
	
	@Test
	public void boolArgTest() {
		tester.expect("boolArg true", true);
		tester.expect("boolArg false", false);
		tester.expectFailure("boolArg");
		tester.expectFailure("boolArg abc");
		tester.expectCompletions("boolArg", "true", "false");
		tester.expectCompletions("boolArg ", "true", "false");
		tester.expectCompletions("boolArg t", "true");
	}
	
	@Test
	public void subcommandCompletionTest() {
		tester.expectCompletions("emptyCommand", "emptySubcommand");
		tester.expectCompletions("emptyCommand ", "emptySubcommand");
		tester.expectCompletions("emptyCommand a");
	}
	
	@Test
	public void intArgConstraintTest() {
		tester.expect("intArgConstraint 1", 1);
		tester.expect("intArgConstraint 0", 0);
		tester.expect("intArgConstraint 100", 100);
		tester.expectFailure("intArgConstraint 101");
		tester.expectFailure("intArgConstraint -1");
	}

	@Test
	public void booleanFlagTest() {
		tester.expect("boolFlag", false);
		tester.expect("boolFlag --flag", true);
		tester.expectCompletions("boolFlag");
		tester.expectCompletions("boolFlag -", "--flag");
	}
	
	@Test
	public void postArgumentTest() {
		tester.expect("baseWithPostArgSub a sub", "a");
		tester.expect("baseWithPostArgSub \"a b c\" sub", "a b c");
		tester.expect("baseWithPostArgSub a subWithPostArgSub 1 sub", "a", 1);
		tester.expectCompletions("baseWithPostArgSub a ", "sub", "subWithPostArgSub");
	}
	
}
