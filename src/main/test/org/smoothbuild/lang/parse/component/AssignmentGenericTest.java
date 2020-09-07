package org.smoothbuild.lang.parse.component;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestedType.A;
import static org.smoothbuild.lang.base.type.TestedType.A_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.A_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.B;
import static org.smoothbuild.lang.base.type.TestedType.B_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.B_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.NOTHING_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRING;
import static org.smoothbuild.lang.base.type.TestedType.STRING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRING_ARRAY2;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING_ARRAY;
import static org.smoothbuild.lang.base.type.TestedType.STRUCT_WITH_STRING_ARRAY2;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;
import static org.smoothbuild.util.Strings.unlines;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.TestedType;

public class AssignmentGenericTest {
  @ParameterizedTest
  @MethodSource("generic_parameter_assignment_test_data")
  public void generic_parameter_assignment(
      boolean allowed, TestedType targetType, TestedType sourceType) {
    String target = targetType.name();
    String source = sourceType.name();
    TestModuleLoader module = module(unlines(
        "String innerFunction(" + target + " target);                          ",
        "outerFunction(" + source + " source) = innerFunction(target=source);  ",
        targetType.declarations(),
        sourceType.declarations()));
    if (allowed) {
      module.loadsSuccessfully();
    } else {
      module.loadsWithError(2,
          "In call to `innerFunction`: Cannot assign argument of type '" + source + "' to " +
              "parameter 'target' of type '" + target + "'.");
    }
  }

  public static List<Arguments> generic_parameter_assignment_test_data() {
    return List.of(
        arguments(true, A, STRING),
        arguments(true, A, STRUCT_WITH_STRING),
        arguments(true, A, NOTHING),
        arguments(true, A, A),
        arguments(true, A, B),
        arguments(true, A, STRING_ARRAY),
        arguments(true, A, STRUCT_WITH_STRING_ARRAY),
        arguments(true, A, NOTHING_ARRAY),
        arguments(true, A, A_ARRAY),
        arguments(true, A, B_ARRAY),
        arguments(true, A, STRING_ARRAY2),
        arguments(true, A, STRUCT_WITH_STRING_ARRAY2),
        arguments(true, A, NOTHING_ARRAY2),
        arguments(true, A, A_ARRAY2),
        arguments(true, A, B_ARRAY2),

        arguments(false, A_ARRAY, STRING),
        arguments(false, A_ARRAY, STRUCT_WITH_STRING),
        arguments(true, A_ARRAY, NOTHING),
        arguments(false, A_ARRAY, A),
        arguments(false, A_ARRAY, B),
        arguments(true, A_ARRAY, STRING_ARRAY),
        arguments(true, A_ARRAY, STRUCT_WITH_STRING_ARRAY),
        arguments(true, A_ARRAY, NOTHING_ARRAY),
        arguments(true, A_ARRAY, A_ARRAY),
        arguments(true, A_ARRAY, B_ARRAY),
        arguments(true, A_ARRAY, STRING_ARRAY2),
        arguments(true, A_ARRAY, STRUCT_WITH_STRING_ARRAY2),
        arguments(true, A_ARRAY, NOTHING_ARRAY2),
        arguments(true, A_ARRAY, A_ARRAY2),
        arguments(true, A_ARRAY, B_ARRAY2),

        arguments(false, A_ARRAY2, STRING),
        arguments(false, A_ARRAY2, STRUCT_WITH_STRING),
        arguments(true, A_ARRAY2, NOTHING),
        arguments(false, A_ARRAY2, A),
        arguments(false, A_ARRAY2, B),
        arguments(false, A_ARRAY2, STRING_ARRAY),
        arguments(false, A_ARRAY2, STRUCT_WITH_STRING_ARRAY),
        arguments(true, A_ARRAY2, NOTHING_ARRAY),
        arguments(false, A_ARRAY2, A_ARRAY),
        arguments(false, A_ARRAY2, B_ARRAY),
        arguments(true, A_ARRAY2, STRING_ARRAY2),
        arguments(true, A_ARRAY2, STRUCT_WITH_STRING_ARRAY2),
        arguments(true, A_ARRAY2, NOTHING_ARRAY2),
        arguments(true, A_ARRAY2, A_ARRAY2),
        arguments(true, A_ARRAY2, B_ARRAY2));
  }
}
