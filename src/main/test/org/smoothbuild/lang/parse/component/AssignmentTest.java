package org.smoothbuild.lang.parse.component;

import static com.google.common.collect.Sets.union;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.base.type.TestedAssignmentSpec.assignment_test_specs;
import static org.smoothbuild.lang.base.type.TestedAssignmentSpec.parameter_assignment_test_specs;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.TestedAssignmentSpec;
import org.smoothbuild.lang.base.type.TestedType;
import org.smoothbuild.lang.base.type.Typing;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.impl.FunctionSType;
import org.smoothbuild.testing.TestingContextImpl;
import org.smoothbuild.testing.TestingModuleLoader;

public class AssignmentTest extends TestingContextImpl {
  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void value_body_type_is_assignable_to_declared_type(TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target();
    TestedType source = testSpec.source();
    String sourceCode = unlines(
        target.name() + " result = " + source.literal() + ";",
        testSpec.declarations(),
        "@Native(\"impl\")",
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
        testSpec.typeDeclarations(),
        "@Native(\"impl\")",
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
    TestingModuleLoader module = module(unlines(
        "@Native(\"impl\")",
        targetType.name() + " innerFunction(" + targetType.name() + " target);     ",
        "outerFunction(" + sourceType.name() + " source) = innerFunction(source);  ",
        testSpec.typeDeclarations()));
    if (testSpec.allowed()) {
      module.loadsSuccessfully();
    } else {
      Type type = targetType.type();
      FunctionType functionType = new FunctionSType(type, list(type));
      module.loadsWithError(3, "In call to function with type " + functionType.q()
          + ": Cannot assign argument of type " + sourceType.q()
          + " to parameter `target` of type " + targetType.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("parameter_assignment_test_data")
  public void argument_type_is_assignable_to_named_parameter_type(TestedAssignmentSpec testSpec) {
    TestedType targetType = testSpec.target();
    TestedType sourceType = testSpec.source();
    TestingModuleLoader module = module(unlines(
        "@Native(\"impl\")",
        targetType.name() + " innerFunction(" + targetType.name() + " target);            ",
        "outerFunction(" + sourceType.name() + " source) = innerFunction(target=source);  ",
        testSpec.typeDeclarations()));
    if (testSpec.allowed()) {
      module.loadsSuccessfully();
    } else {
      Type type = targetType.type();
      FunctionType functionType = new FunctionSType(type, list(type));
      module.loadsWithError(3,
          "In call to function with type " + functionType.q() +
              ": Cannot assign argument of type " + sourceType.q()
              + " to parameter `target` of type " + targetType.q() + ".");
    }
  }

  private static List<TestedAssignmentSpec> parameter_assignment_test_data() {
    return parameter_assignment_test_specs(false);
  }

  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void default_argument_type_is_assignable_to_parameter_type(TestedAssignmentSpec testSpec) {
    TestedType target = testSpec.target();
    TestedType source = testSpec.source();
    String sourceCode = unlines(
        "myFunction(" + target.name() + " param = " + source.literal() + ") = param; ",
        testSpec.declarations(),
        "@Native(\"impl\")",
        "Bool true;");
    if (testSpec.allowed()) {
      module(sourceCode)
          .loadsSuccessfully();
    } else {
      module(sourceCode)
          .loadsWithError(1, "Parameter `param` is of type " + target.q()
               + " so it cannot have default argument of type " + source.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("array_element_assignment_test_specs")
  public void array_literal_element_types_is_assignable_to_common_super_type(
      TestedType type1, TestedType type2, Type joinType) {
    String sourceCode = unlines(
        "[" + joinType.name() + "] result = [" + type1.literal() + ", " + type2.literal() + "];",
        join("\n", union(type1.allDeclarations(), type2.allDeclarations())),
        "@Native(\"impl\")",
        "Bool true;");
    module(sourceCode)
        .loadsSuccessfully();
  }

  private static List<Arguments> array_element_assignment_test_specs() {
    TestingContextImpl context = new TestingContextImpl();
    Typing typing = context.typing();
    TypeFactory factory = context.typeFactory();
    ArrayList<Arguments> result = new ArrayList<>();
    for (TestedType type1 : TestedType.TESTED_MONOTYPES) {
      for (TestedType type2 : TestedType.TESTED_MONOTYPES) {
        Type commonSuperType = typing.mergeUp(type1.type(), type2.type());
        if (!typing.contains(commonSuperType, factory.any())) {
          result.add(Arguments.of(type1, type2, commonSuperType));
        }
      }
    }
    return result;
  }

  private static List<TestedAssignmentSpec> without_polytypes_test_specs() {
    return assignment_test_specs(false)
        .stream()
        .filter(a -> !(a.target().type().isPolytype() || a.source().type().isPolytype()))
        .collect(toList());
  }

  private static List<TestedAssignmentSpec> test_specs() {
    return assignment_test_specs(false);
  }
}
