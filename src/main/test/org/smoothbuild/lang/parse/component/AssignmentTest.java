package org.smoothbuild.lang.parse.component;

import static com.google.common.collect.Sets.union;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.TestedAssignmentSpec.assignment_test_specs;
import static org.smoothbuild.lang.base.type.TestedAssignmentSpec.parameter_assignment_test_specs;
import static org.smoothbuild.lang.parse.component.TestModuleLoader.module;
import static org.smoothbuild.util.Strings.unlines;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.TestedAssignmentSpec;
import org.smoothbuild.lang.base.type.TestedType;
import org.smoothbuild.lang.base.type.Type;

public class AssignmentTest {
  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void value_body_type_is_assignable_to_declared_type(TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target();
    TestedType source = testSpec.source();
    String sourceCode = unlines(
        target.name() + " result = " + source.literal() + ";",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed()) {
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
    TestedType target = testSpec.target();
    TestedType source = testSpec.source();
    String sourceCode = unlines(
        "%s myFunction(%s param, %s probablyPolytype) = param;"
            .formatted(target.name(), source.name(), target.name()),
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed()) {
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
    TestedType targetType = testSpec.target();
    TestedType sourceType = testSpec.source();
    TestModuleLoader module = module(unlines(
        "String innerFunction(" + targetType.name() + " target);                          ",
        "outerFunction(" + sourceType.name() + " source) = innerFunction(source);  ",
        testSpec.declarations()));
    if (testSpec.allowed()) {
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
    TestedType targetType = testSpec.target();
    TestedType sourceType = testSpec.source();
    TestModuleLoader module = module(unlines(
        "String innerFunction(" + targetType.name() + " target);                          ",
        "outerFunction(" + sourceType.name() + " source) = innerFunction(target=source);  ",
        testSpec.declarations()));
    if (testSpec.allowed()) {
      module.loadsSuccessfully();
    } else {
      module.loadsWithError(2,
          "In call to `innerFunction`: Cannot assign argument of type " + sourceType.q()
              + " to parameter `target` of type " + targetType.q() + ".");
    }
  }

  private static List<TestedAssignmentSpec> parameter_assignment_test_data() {
    return filterOutFunctions(parameter_assignment_test_specs());
  }

  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void default_value_type_is_assignable_to_parameter_type(TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target();
    TestedType source = testSpec.source();
    String sourceCode = unlines(
        "myFunction(" + target.name() + " param = " + source.literal() + ") = param; ",
        testSpec.declarations(),
        "Bool true;");
    if (testSpec.allowed()) {
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
      TestedType type1, TestedType type2, Type joinType) {
    String sourceCode = unlines(
        "[" + joinType.name() + "] result = [" + type1.literal() + ", " + type2.literal() + "];",
        join("\n", union(type1.declarations(), type2.declarations())),
        "Bool true;");
    module(sourceCode)
        .loadsSuccessfully();
  }

  public static List<Arguments> array_element_assignment_test_specs() {
    ArrayList<Arguments> result = new ArrayList<>();
    for (TestedType type1 : TestedType.TESTED_MONOTYPES) {
      for (TestedType type2 : TestedType.TESTED_MONOTYPES) {
        result.add(Arguments.of(type1, type2, type1.type().mergeWith(type2.type(), UPPER)));
      }
    }
    return result;
  }

  private static List<TestedAssignmentSpec> without_polytypes_test_specs() {
    return filterOutFunctions(assignment_without_polytypes_test_specs());
  }

  public static List<TestedAssignmentSpec> assignment_without_polytypes_test_specs() {
    return assignment_test_specs()
        .stream()
        .filter(a -> !(a.target().type().isPolytype() || a.source().type().isPolytype()))
        .collect(toList());
  }

  private static List<TestedAssignmentSpec> test_specs() {
    return filterOutFunctions(assignment_test_specs());
  }

  // TODO remove once functions are implemented on language level
  private static List<TestedAssignmentSpec> filterOutFunctions(List<TestedAssignmentSpec> specs) {
    return specs
        .stream()
        .filter(t -> !isFunctionOrContainsFunction(t.source().type()))
        .filter(t -> !isFunctionOrContainsFunction(t.target().type()))
        .collect(toList());
  }

  private static boolean isFunctionOrContainsFunction(Type type) {
    return type instanceof FunctionType || containsFunction(type);
  }

  private static boolean containsFunction(Type type) {
    return type instanceof ArrayType arrayType && isFunctionOrContainsFunction(arrayType.elemType());
  }
}
