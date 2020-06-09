package org.smoothbuild.acceptance.lang.assign;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class GenericAssignmentTest extends AcceptanceTestCase {
  @ParameterizedTest
  @MethodSource("generic_parameter_assignment_test_data")
  public void generic_parameter_assignment(
      boolean allowed, String targetType, String sourceType, List<String> structs)
      throws IOException {
    String declarations = structs.stream()
        .map(s -> s.concat("{}\n"))
        .collect(joining());
    createUserModule(declarations,
        "String innerFunction(" + targetType + " target) = 'abc';                  ",
        "outerFunction(" + sourceType + " source) = innerFunction(target=source);  ");
    runSmoothList();
    if (allowed) {
      assertFinishedWithSuccess();
    } else {
      assertSysOutContainsParseError(structs.size() + 3,
          "In call to `innerFunction`: Cannot assign argument of type '" + sourceType + "' to " +
              "parameter 'target' of type '" + targetType + "'.");
    }
  }

  public static List<Arguments> generic_parameter_assignment_test_data() {
    return List.of(
        arguments(true, "A", "String", List.of()),
        arguments(true, "A", "MyStruct", List.of("MyStruct")),
        arguments(true, "A", "Nothing", List.of()),
        arguments(true, "A", "A", List.of()),
        arguments(true, "A", "B", List.of()),
        arguments(true, "A", "[String]", List.of()),
        arguments(true, "A", "[MyStruct]", List.of("MyStruct")),
        arguments(true, "A", "[Nothing]", List.of()),
        arguments(true, "A", "[A]", List.of()),
        arguments(true, "A", "[B]", List.of()),
        arguments(true, "A", "[[String]]", List.of()),
        arguments(true, "A", "[[MyStruct]]", List.of("MyStruct")),
        arguments(true, "A", "[[Nothing]]", List.of()),
        arguments(true, "A", "[[A]]", List.of()),
        arguments(true, "A", "[[B]]", List.of()),

        arguments(false, "[A]", "String", List.of()),
        arguments(false, "[A]", "MyStruct", List.of("MyStruct")),
        arguments(true, "[A]", "Nothing", List.of()),
        arguments(false, "[A]", "A", List.of()),
        arguments(false, "[A]", "B", List.of()),
        arguments(true, "[A]", "[String]", List.of()),
        arguments(true, "[A]", "[MyStruct]", List.of("MyStruct")),
        arguments(true, "[A]", "[Nothing]", List.of()),
        arguments(true, "[A]", "[A]", List.of()),
        arguments(true, "[A]", "[B]", List.of()),
        arguments(true, "[A]", "[[String]]", List.of()),
        arguments(true, "[A]", "[[MyStruct]]", List.of("MyStruct")),
        arguments(true, "[A]", "[[Nothing]]", List.of()),
        arguments(true, "[A]", "[[A]]", List.of()),
        arguments(true, "[A]", "[[B]]", List.of()),

        arguments(false, "[[A]]", "String", List.of()),
        arguments(false, "[[A]]", "MyStruct", List.of("MyStruct")),
        arguments(true, "[[A]]", "Nothing", List.of()),
        arguments(false, "[[A]]", "A", List.of()),
        arguments(false, "[[A]]", "B", List.of()),
        arguments(false, "[[A]]", "[String]", List.of()),
        arguments(false, "[[A]]", "[MyStruct]", List.of("MyStruct")),
        arguments(true, "[[A]]", "[Nothing]", List.of()),
        arguments(false, "[[A]]", "[A]", List.of()),
        arguments(false, "[[A]]", "[B]", List.of()),
        arguments(true, "[[A]]", "[[String]]", List.of()),
        arguments(true, "[[A]]", "[[MyStruct]]", List.of("MyStruct")),
        arguments(true, "[[A]]", "[[Nothing]]", List.of()),
        arguments(true, "[[A]]", "[[A]]", List.of()),
        arguments(true, "[[A]]", "[[B]]", List.of()));
  }
}
