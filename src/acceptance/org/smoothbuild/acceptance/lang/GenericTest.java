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

  private static Case allowedAssignment(String assignedType, String assigneeType,
      String... structs) {
    return newCase("can assign " + assigneeType + " to param with type " + assignedType, () -> {
      executeTest(assignedType, assigneeType, (test) -> test.thenFinishedWithSuccess(), structs);
    });
  }

  private static Case illegalAssignment(String assignedType, String assigneeType,
      String... structs) {
    return newCase("can't assign " + assigneeType + " to param with type " + assignedType, () -> {
      Consumer<AcceptanceTestCase> asserter = (test) -> {
        test.thenOutputContainsError(structs.length + 2,
            "Type mismatch, cannot convert argument 'assignee' of type '"
                + assigneeType + "' to '" + assignedType + "'.");
      };
      executeTest(assignedType, assigneeType, asserter, structs);
    });
  }

  private static void executeTest(String assignedType, String assigneeType,
      Consumer<AcceptanceTestCase> asserter, String... structs) throws IOException {
    AcceptanceTestCase test = new AcceptanceTestCase() {};
    test.init();
    String declarations = stream(structs)
        .map(s -> s.concat("{}\n"))
        .collect(joining());
    try {
      test.givenScript(declarations
          + "String innerFunction(" + assignedType + " assignee) = 'abc';                     \n"
          + "outerFunction(" + assigneeType + " assigned) = innerFunction(assignee=assigned); \n");
      test.whenSmoothList();
      asserter.accept(test);
    } finally {
      test.destroy();
    }
  }
}
