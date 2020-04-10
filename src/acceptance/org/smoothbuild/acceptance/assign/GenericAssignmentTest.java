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
        allowedAssignment("A", "String"),
        allowedAssignment("A", "MyStruct", "MyStruct"),
        allowedAssignment("A", "Nothing"),
        allowedAssignment("A", "A"),
        allowedAssignment("A", "B"),
        allowedAssignment("A", "[String]"),
        allowedAssignment("A", "[MyStruct]", "MyStruct"),
        allowedAssignment("A", "[Nothing]"),
        allowedAssignment("A", "[A]"),
        allowedAssignment("A", "[B]"),
        allowedAssignment("A", "[[String]]"),
        allowedAssignment("A", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("A", "[[Nothing]]"),
        allowedAssignment("A", "[[A]]"),
        allowedAssignment("A", "[[B]]"),

        illegalAssignment("[A]", "String"),
        illegalAssignment("[A]", "MyStruct", "MyStruct"),
        allowedAssignment("[A]", "Nothing"),
        illegalAssignment("[A]", "A"),
        illegalAssignment("[A]", "B"),
        allowedAssignment("[A]", "[String]"),
        allowedAssignment("[A]", "[MyStruct]", "MyStruct"),
        allowedAssignment("[A]", "[Nothing]"),
        allowedAssignment("[A]", "[A]"),
        allowedAssignment("[A]", "[B]"),
        allowedAssignment("[A]", "[[String]]"),
        allowedAssignment("[A]", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("[A]", "[[Nothing]]"),
        allowedAssignment("[A]", "[[A]]"),
        allowedAssignment("[A]", "[[B]]"),

        illegalAssignment("[[A]]", "String"),
        illegalAssignment("[[A]]", "MyStruct", "MyStruct"),
        allowedAssignment("[[A]]", "Nothing"),
        illegalAssignment("[[A]]", "A"),
        illegalAssignment("[[A]]", "B"),
        illegalAssignment("[[A]]", "[String]"),
        illegalAssignment("[[A]]", "[MyStruct]", "MyStruct"),
        allowedAssignment("[[A]]", "[Nothing]"),
        illegalAssignment("[[A]]", "[A]"),
        illegalAssignment("[[A]]", "[B]"),
        allowedAssignment("[[A]]", "[[String]]"),
        allowedAssignment("[[A]]", "[[MyStruct]]", "MyStruct"),
        allowedAssignment("[[A]]", "[[Nothing]]"),
        allowedAssignment("[[A]]", "[[A]]"),
        allowedAssignment("[[A]]", "[[B]]")));
  }

  private static Case allowedAssignment(String targetType, String sourceType, String... structs) {
    return newCase("can assign " + sourceType + " to param with type " + targetType, () -> {
      executeTest(targetType, sourceType, (test) -> test.thenFinishedWithSuccess(), structs);
    });
  }

  private static Case illegalAssignment(String targetType, String sourceType, String... structs) {
    return newCase("can't assign " + sourceType + " to param with type " + targetType, () -> {
      Consumer<AcceptanceTestCase> asserter = (test) -> {
        test.thenSysOutContainsError(structs.length + 3,
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
      test.givenScript(declarations,
          "String innerFunction(" + targetType + " target) = 'abc';                  ",
          "outerFunction(" + sourceType + " source) = innerFunction(target=source);  ");
      test.whenSmoothList();
      asserter.accept(test);
    } finally {
      test.destroy();
    }
  }
}
