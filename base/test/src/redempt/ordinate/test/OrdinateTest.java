package redempt.ordinate.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

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
	public void optionalArgTest() {
		tester.expect("optionalArg", new Object[] {null});
		tester.expect("optionalArg 4", 4);
		tester.expectFailure("optionalArg a");
		tester.expect("optionalArgWithDefault", 100);
		tester.expect("optionalArgWithDefault 4", 4);
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
		tester.expect("boolFlag -f", true);
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
	
	@Test
	public void intFlagTest() {
		tester.expect("switchFlag --flag true", true);
		tester.expect("switchFlag", new Object[] {null});
		tester.expect("switchFlag -f true", true);
		tester.expectCompletions("switchFlag -", "--flag");
		tester.expectCompletions("switchFlag --flag ", "true", "false");
	}
	
	@Test
	public void defaultConstrainedFlagTest() {
		tester.expect("defaultConstrainedFlag", 4);
		tester.expect("defaultConstrainedFlag --flag 10", 10);
		tester.expectFailure("defaultConstrainedFlag --flag -1");
		tester.expectFailure("defaultConstrainedFlag --flag 101");
	}
	
	@Test
	public void consumingTest() {
		tester.expect("consuming a b c", "a b c");
		tester.expectFailure("consuming");
		tester.expect("optionalConsuming a b c", "a b c");
		tester.expect("optionalConsuming", new Object[] {null});
	}
	
	@Test
	public void varargTest() {
		tester.expect("vararg true false true", Arrays.asList(true, false, true));
		tester.expectFailure("vararg");
		tester.expect("optionalVararg 1 2 3", Arrays.asList(1, 2, 3));
		tester.expect("optionalVararg", Collections.emptyList());
		tester.expectCompletions("vararg ", "true", "false");
		tester.expectCompletions("vararg true ", "true", "false");
	}
	
	@Test
	public void contextTest() {
		tester.expect("withContext abc", "test", "abc");
	}
	
	@Test
	public void assertTest() {
		tester.expect("passAssert");
		tester.expectFailure("failAssert");
	}
	
	@Test
	public void multiOptionalTest() {
		tester.expect("multiOptional", false, 0);
		tester.expect("multiOptional true", true, 0);
		tester.expect("multiOptional 10", false, 10);
	}
	
}
