package org.smoothbuild.lang.parse.component;

import static com.google.common.collect.Sets.union;
import static java.lang.String.join;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.TestedAssignCases;
import org.smoothbuild.lang.base.type.TestedAssignSpecS;
import org.smoothbuild.lang.base.type.TestedTS;
import org.smoothbuild.lang.base.type.TestedTSF;
import org.smoothbuild.lang.base.type.api.FuncT;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.FuncTS;
import org.smoothbuild.lang.base.type.impl.TypeSF;
import org.smoothbuild.lang.base.type.impl.TypingS;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.TestingModLoader;

public class AssignmentTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void value_body_type_is_assignable_to_declared_type(TestedAssignSpecS testSpec) {
    TestedTS target = testSpec.target();
    TestedTS source = testSpec.source();
    String sourceCode = unlines(
        target.name() + " result = " + source.literal() + ";",
        testSpec.declarations());
    if (testSpec.allowed()) {
      module(sourceCode)
          .loadsWithSuccess();
    } else {
      module(sourceCode)
          .loadsWithError(1, "`result` has body which type is " + source.q()
               + " and it is not convertible to its declared type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("test_specs")
  public void func_body_type_is_assignable_to_declared_type(TestedAssignSpecS testSpec) {
    TestedTS target = testSpec.target();
    TestedTS source = testSpec.source();
    String sourceCode = unlines(
        "%s myFunc(%s param, %s probablyPolytype) = param;"
            .formatted(target.name(), source.name(), target.name()),
        testSpec.typeDeclarations());
    if (testSpec.allowed()) {
      module(sourceCode)
          .loadsWithSuccess();
    } else {
      module(sourceCode)
          .loadsWithError(1, "`myFunc` has body which type is " + source.q()
               + " and it is not convertible to its declared type " + target.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("param_assignment_test_data")
  public void arg_type_is_assignable_to_param_type(TestedAssignSpecS testSpec) {
    TestedTS targetT = testSpec.target();
    TestedTS sourceT = testSpec.source();
    TestingModLoader module = module(unlines(
        "@Native(\"impl\")",
        targetT.name() + " innerFunc(" + targetT.name() + " target);     ",
        "outerFunc(" + sourceT.name() + " source) = innerFunc(source);  ",
        testSpec.typeDeclarations()));
    if (testSpec.allowed()) {
      module.loadsWithSuccess();
    } else {
      var type = targetT.type();
      FuncT funcT = new FuncTS(type, list(type));
      module.loadsWithError(3, "In call to function with type " + funcT.q()
          + ": Cannot assign argument of type " + sourceT.q()
          + " to parameter `target` of type " + targetT.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("param_assignment_test_data")
  public void arg_type_is_assignable_to_named_param_type(TestedAssignSpecS testSpec) {
    TestedTS targetT = testSpec.target();
    TestedTS sourceT = testSpec.source();
    TestingModLoader module = module(unlines(
        "@Native(\"impl\")",
        targetT.name() + " innerFunc(" + targetT.name() + " target);            ",
        "outerFunc(" + sourceT.name() + " source) = innerFunc(target=source);  ",
        testSpec.typeDeclarations()));
    if (testSpec.allowed()) {
      module.loadsWithSuccess();
    } else {
      var type = targetT.type();
      FuncT funcT = new FuncTS(type, list(type));
      module.loadsWithError(3,
          "In call to function with type " + funcT.q() +
              ": Cannot assign argument of type " + sourceT.q()
              + " to parameter `target` of type " + targetT.q() + ".");
    }
  }

  private static List<TestedAssignSpecS> param_assignment_test_data() {
    return TestedAssignCases.INSTANCE_S.param_assignment_test_specs(false);
  }

  @ParameterizedTest
  @MethodSource("without_polytypes_test_specs")
  public void default_arg_type_is_assignable_to_param_type(TestedAssignSpecS testSpec) {
    TestedTS target = testSpec.target();
    TestedTS source = testSpec.source();
    String sourceCode = unlines(
        "myFunc(" + target.name() + " param = " + source.literal() + ") = param; ",
        testSpec.declarations());
    if (testSpec.allowed()) {
      module(sourceCode)
          .loadsWithSuccess();
    } else {
      module(sourceCode)
          .loadsWithError(1, "Parameter `param` is of type " + target.q()
               + " so it cannot have default argument of type " + source.q() + ".");
    }
  }

  @ParameterizedTest
  @MethodSource("array_elem_assignment_test_specs")
  public void array_literal_elem_types_is_assignable_to_common_super_type(
      TestedTS type1, TestedTS type2, Type joinT) {
    String sourceCode = unlines(
        "[" + joinT.name() + "] result = [" + type1.literal() + ", " + type2.literal() + "];",
        join("\n", union(type1.allDeclarations(), type2.allDeclarations())));
    module(sourceCode)
        .loadsWithSuccess();
  }

  private static List<Arguments> array_elem_assignment_test_specs() {
    TestingContext context = new TestingContext();
    TypingS typing = context.typingS();
    TypeSF factory = context.typeSF();
    ArrayList<Arguments> result = new ArrayList<>();
    for (TestedTS type1 : TestedTSF.TESTED_MONOTYPES) {
      for (TestedTS type2 : TestedTSF.TESTED_MONOTYPES) {
        var commonSuperT = typing.mergeUp(type1.type(), type2.type());
        if (!typing.contains(commonSuperT, factory.any())) {
          result.add(Arguments.of(type1, type2, commonSuperT));
        }
      }
    }
    return result;
  }

  private static List<TestedAssignSpecS> without_polytypes_test_specs() {
    return TestedAssignCases.INSTANCE_S.assignment_test_specs(false)
        .stream()
        .filter(a -> !(a.target().type().isPolytype() || a.source().type().isPolytype()))
        .collect(toList());
  }

  private static List<TestedAssignSpecS> test_specs() {
    return TestedAssignCases.INSTANCE_S.assignment_test_specs(false);
  }
}
