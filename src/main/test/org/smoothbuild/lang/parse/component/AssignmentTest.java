package org.smoothbuild.lang.parse.component;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.base.type.TestedAssignmentSpec.assignment_test_specs;
import static org.smoothbuild.lang.base.type.TestedAssignmentSpec.parameter_assignment_test_specs;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.TestedAssignmentSpec;
import org.smoothbuild.lang.base.type.TestedType;

public class AssignmentTest {
  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void value_body_type_is_assignable_to_declared_type(TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        target.name() + " result = " + source.literal() + ";",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1, "`result` has body which type is " + source.q()
               + " and it is not convertible to its declared type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("test_specs")
  public void function_body_type_is_assignable_to_declared_type(TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "%s myFunction(%s param, %s probablyPolytype) = param;"
            .formatted(target.name(), source.name(), target.name()),
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1, "`myFunction` has body which type is " + source.q()
               + " and it is not convertible to its declared type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("parameter_assignment_test_data")
  public void argument_type_is_assignable_to_parameter_type(TestedAssignmentSpec testSpec) {
    TestedType targetType = testSpec.target;
    TestedType sourceType = testSpec.source;
    TestModuleLoader module = module(unlines(
        "String innerFunction(" + targetType.name() + " target);                          ",
        "outerFunction(" + sourceType.name() + " source) = innerFunction(source);  ",
        testSpec.declarations()));
    if (testSpec.allowed) {
      module.loadsSuccessfully();
    } else {
      module.loadsWithError(2,
          "In call to `innerFunction`: Cannot assign argument of type " + sourceType.q()
              + " to parameter `target` of type " + targetType.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("parameter_assignment_test_data")
  public void argument_type_is_assignable_to_named_parameter_type(TestedAssignmentSpec testSpec) {
    TestedType targetType = testSpec.target;
    TestedType sourceType = testSpec.source;
    TestModuleLoader module = module(unlines(
        "String innerFunction(" + targetType.name() + " target);                          ",
        "outerFunction(" + sourceType.name() + " source) = innerFunction(target=source);  ",
        testSpec.declarations()));
    if (testSpec.allowed) {
      module.loadsSuccessfully();
    } else {
      module.loadsWithError(2,
          "In call to `innerFunction`: Cannot assign argument of type " + sourceType.q()
              + " to parameter `target` of type " + targetType.q() + ".");
    }
  }

  private static List<TestedAssignmentSpec> parameter_assignment_test_data() {
    return parameter_assignment_test_specs();
  }

  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void default_value_type_is_assignable_to_parameter_type(TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "myFunction(" + target.name() + " param = " + source.literal() + ") = param; ",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1, "Parameter `param` is of type " + target.q()
               + " so it cannot have default value of type " + source.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("array_element_assignment_test_specs")
  public void array_literal_element_types_is_assignable_to_common_super_type(
      TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target;
    TestedType source = testSpec.source;
    String sourceCode = unlines(
        "result = [" + source.literal() + ", " + target.literal() + "];",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1,
              "Array cannot contain elements of incompatible types. First element has type " +
               source.q() + " while element at index 1 has type " + target.q() + ".");
    }
  }

  /**
   * Type compatibility between two array literal elements is satisfied when type of any of those
   * elements is assignable to the type of the other.
   */
  public static Stream<TestedAssignmentSpec> array_element_assignment_test_specs() {
    var map = new HashMap<TestedType, Map<TestedType, TestedAssignmentSpec>>();
    without_polytypes_test_specs().forEach(spec -> {
      map.computeIfAbsent(spec.source, testedType -> new HashMap<>());
      map.get(spec.source).put(spec.target, spec);
    });

    var result = new ArrayList<TestedAssignmentSpec>();
    without_polytypes_test_specs().forEach(spec -> {
      TestedAssignmentSpec reversed = map.get(spec.target).get(spec.source);
      boolean reversedAllowed = reversed != null && reversed.allowed;
      if (!spec.allowed && reversedAllowed) {
        result.add(new TestedAssignmentSpec(spec, true));
      } else {
        result.add(spec);
      }
    });
    return result.stream();
  }

  private static List<TestedAssignmentSpec> without_polytypes_test_specs() {
    return assignment_without_polytypes_test_specs();
  }

  public static List<TestedAssignmentSpec> assignment_without_polytypes_test_specs() {
    return assignment_test_specs()
        .stream()
        .filter(a -> !(a.target.type().isPolytype() || a.source.type().isPolytype()))
        .collect(toList());
  }

  private static List<TestedAssignmentSpec> test_specs() {
    return assignment_test_specs();
  }
}
