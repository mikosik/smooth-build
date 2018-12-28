package org.smoothbuild.acceptance.assign;

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
public class GenericAssignmentTest extends AcceptanceTestCase {
  @Quackery
  public static Suite generic_param_assignment() throws Exception {
    return suite("generic param assignment").addAll(asList(
        allowedAssignment("a", "String"),
        allowedAssignment("a", "MyStruct", "MyStruct"),
        allowedAssignment("a", "Nothing"),
        allowedAssignment("a", "a"),
        allowedAssignment("a", "b"),
        allowedAssignment("a", "[String]"),
        allowedAssignment("a", "[MyStruct]", "MyStruct"),
        allowedAssignment("a", "[Nothing]"),
        allowedAssignment("a", "[a]"),
        allowedAssignment("a", "[b]"),
        allowedAssignment("a", "[[String]]"),
        allowedAssignment("a", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("a", "[[Nothing]]"),
        allowedAssignment("a", "[[a]]"),
        allowedAssignment("a", "[[b]]"),

        illegalAssignment("[a]", "String"),
        illegalAssignment("[a]", "MyStruct", "MyStruct"),
        allowedAssignment("[a]", "Nothing"),
        illegalAssignment("[a]", "a"),
        illegalAssignment("[a]", "b"),
        allowedAssignment("[a]", "[String]"),
        allowedAssignment("[a]", "[MyStruct]", "MyStruct"),
        allowedAssignment("[a]", "[Nothing]"),
        allowedAssignment("[a]", "[a]"),
        allowedAssignment("[a]", "[b]"),
        allowedAssignment("[a]", "[[String]]"),
        allowedAssignment("[a]", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("[a]", "[[Nothing]]"),
        allowedAssignment("[a]", "[[a]]"),
        allowedAssignment("[a]", "[[b]]"),

        illegalAssignment("[[a]]", "String"),
        illegalAssignment("[[a]]", "MyStruct", "MyStruct"),
        allowedAssignment("[[a]]", "Nothing"),
        illegalAssignment("[[a]]", "a"),
        illegalAssignment("[[a]]", "b"),
        illegalAssignment("[[a]]", "[String]"),
        illegalAssignment("[[a]]", "[MyStruct]", "MyStruct"),
        allowedAssignment("[[a]]", "[Nothing]"),
        illegalAssignment("[[a]]", "[a]"),
        illegalAssignment("[[a]]", "[b]"),
        allowedAssignment("[[a]]", "[[String]]"),
        allowedAssignment("[[a]]", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("[[a]]", "[[Nothing]]"),
        allowedAssignment("[[a]]", "[[a]]"),
        allowedAssignment("[[a]]", "[[b]]")));
  }

  private static Case allowedAssignment(String targetType, String sourceType, String... structs) {
    return newCase("can assign " + sourceType + " to param with type " + targetType, () -> {
      executeTest(targetType, sourceType, (test) -> test.thenFinishedWithSuccess(), structs);
    });
  }

  private static Case illegalAssignment(String targetType, String sourceType, String... structs) {
    return newCase("can't assign " + sourceType + " to param with type " + targetType, () -> {
      Consumer<AcceptanceTestCase> asserter = (test) -> {
        test.thenOutputContainsError(structs.length + 2,
            "Cannot assign argument of type '" + sourceType + "' to " +
                "parameter 'target' of type '" + targetType + "'.");
      };
      executeTest(targetType, sourceType, asserter, structs);
    });
  }

  private static void executeTest(String targetType, String sourceType,
      Consumer<AcceptanceTestCase> asserter, String... structs) throws IOException {
    AcceptanceTestCase test = new AcceptanceTestCase() {};
    test.init();
    String declarations = stream(structs)
        .map(s -> s.concat("{}\n"))
        .collect(joining());
    try {
      test.givenScript(declarations
          + "String innerFunction(" + targetType + " target) = 'abc';                     \n"
          + "outerFunction(" + sourceType + " source) = innerFunction(target=source); \n");
      test.whenSmoothList();
      asserter.accept(test);
    } finally {
      test.destroy();
    }
  }
}
