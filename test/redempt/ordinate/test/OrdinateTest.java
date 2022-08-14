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
		tester.expect("oneStringArg abc", "abc");
		tester.expectFailure("oneStringArg");
	}

}
