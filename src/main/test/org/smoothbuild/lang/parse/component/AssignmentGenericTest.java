package org.smoothbuild.lang.parse.component;

import static org.smoothbuild.lang.base.type.TestedAssignmentSpec.parameter_assignment_test_specs;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;
import static org.smoothbuild.util.Strings.unlines;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.TestedType;

public class AssignmentGenericTest {
  @ParameterizedTest
  @MethodSource("parameter_assignment_test_data")
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
          "In call to `innerFunction`: Cannot assign argument of type " + sourceType.q()
              + " to parameter `target` of type " + targetType.q() + ".");
    }
  }

  private static Stream<Arguments> parameter_assignment_test_data() {
    return parameter_assignment_test_specs();
  }
}
