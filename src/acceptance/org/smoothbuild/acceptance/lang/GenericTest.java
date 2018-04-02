package org.smoothbuild.acceptance.lang;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;

import java.io.IOException;
import java.util.function.Consumer;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.smoothbuild.acceptance.AcceptanceTestCase;

@RunWith(QuackeryRunner.class)
public class GenericTest extends AcceptanceTestCase {
  @Quackery
  public static Suite generic_param_assignment() throws Exception {
    return suite("generic param assignment").addAll(asList(
        allowedAssignment("a", "'def'"),
        allowedAssignment("a", "['def']"),
        allowedAssignment("a", "[['def']]"),
        allowedAssignment("a", "MyStruct", "MyStruct"),
        allowedAssignment("a", "[MyStruct]", "MyStruct"),
        allowedAssignment("a", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("a", "[]"),
        allowedAssignment("a", "[[]]"),

        illegalAssignment("[a]", "'def'", "String"),
        allowedAssignment("[a]", "['def']"),
        allowedAssignment("[a]", "[['def']]"),
        illegalAssignment("[a]", "MyStruct", "MyStruct", "MyStruct"),
        allowedAssignment("[a]", "[MyStruct]", "MyStruct"),
        allowedAssignment("[a]", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("[a]", "[]"),
        allowedAssignment("[a]", "[[]]"),

        illegalAssignment("[[a]]", "'def'", "String"),
        illegalAssignment("[[a]]", "['def']", "[String]"),
        allowedAssignment("[[a]]", "[['def']]"),
        illegalAssignment("[[a]]", "MyStruct", "MyStruct", "MyStruct"),
        illegalAssignment("[[a]]", "[MyStruct]", "[MyStruct]", "MyStruct"),
        allowedAssignment("[[a]]", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("[[a]]", "[]"),
        allowedAssignment("[[a]]", "[[]]")));
  }

  private static Case allowedAssignment(String paramType, String arg, String... structs) {
    return newCase("can assign " + arg + " to param with type " + paramType, () -> {
      executeTest(paramType, arg, (test) -> test.thenFinishedWithSuccess(), structs);
    });
  }

  private static Case illegalAssignment(String paramType, String arg, String argType,
      String... structs) {
    return newCase("can't assign " + arg + " to param with type " + paramType, () -> {
      Consumer<AcceptanceTestCase> asserter = (test) -> {
        test.thenOutputContainsError(structs.length + 2,
            "Type mismatch, cannot convert argument 'value' of type '"
                + argType + "' to '" + paramType + "'.");
      };
      executeTest(paramType, arg, asserter, structs);
    });
  }

  private static void executeTest(String paramType, String arg,
      Consumer<AcceptanceTestCase> asserter, String... structs)
      throws IOException {
    AcceptanceTestCase test = new AcceptanceTestCase() {};
    test.init();
    String declarations = stream(structs)
        .map(s -> s.concat("{}\n"))
        .collect(joining());
    try {
      test.givenScript(declarations
          + "String myFunction(" + paramType + " value) = 'abc';   \n"
          + "result = myFunction(value=" + arg + ");           \n");
      test.whenSmoothList();
      asserter.accept(test);
    } finally {
      test.destroy();
    }
  }
}
