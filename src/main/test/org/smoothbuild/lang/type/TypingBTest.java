package org.smoothbuild.lang.type;

import static org.smoothbuild.testing.type.TestedAssignCases.INSTANCE_B;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.VarBounds;
import org.smoothbuild.testing.type.TestedAssignSpec;
import org.smoothbuild.testing.type.TestedTB;

public class TypingBTest {
  private final static TypingTestCases<TypeB, TestedTB> TYPING_TEST_CASES =
      new TypingTestCases<>(INSTANCE_B);

  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void testContains(TypeB type, TypeB contained, boolean expected) {
    TYPING_TEST_CASES.testContains(type, contained, expected);
  }

  public static List<Arguments> contains_test_data() {
    return TYPING_TEST_CASES.contains_test_data();
  }

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignSpec<TestedTB> spec) {
    TYPING_TEST_CASES.isAssignable(spec);
  }

  public static List<? extends TestedAssignSpec<TestedTB>> isAssignable_test_data() {
    return TYPING_TEST_CASES.isAssignable_test_data();
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignSpec<TestedTB> spec) {
    TYPING_TEST_CASES.isParamAssignable(spec);
  }

  public static List<? extends TestedAssignSpec<TestedTB>> isParamAssignable_test_data() {
    return TYPING_TEST_CASES.isParamAssignable_test_data();
  }

  @ParameterizedTest
  @MethodSource("inferVarBounds_test_data")
  public void inferVarBounds(TypeB type, TypeB assigned, VarBounds<TypeB> expected) {
    TYPING_TEST_CASES.inferVarBounds(type, assigned, expected);
  }

  public static List<Arguments> inferVarBounds_test_data() {
    return TYPING_TEST_CASES.inferVarBounds_test_data();
  }

  @ParameterizedTest
  @MethodSource("mapVarsLower_test_data")
  public void mapVarsLower(TypeB type, VarBounds<TypeB> varBounds, TypeB expected) {
    TYPING_TEST_CASES.mapVarsLower(type, varBounds, expected);
  }

  public static List<Arguments> mapVarsLower_test_data() {
    return TYPING_TEST_CASES.mapVarsLower_test_data();
  }

  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_cases")
  public void merge_up_wide_graph(TypeB type1, TypeB type2, TypeB expected) {
    TYPING_TEST_CASES.merge_up_wide_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_up_wide_graph_cases() {
    return TYPING_TEST_CASES.merge_up_wide_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_cases")
  public void merge_up_deep_graph(TypeB type1, TypeB type2, TypeB expected) {
    TYPING_TEST_CASES.merge_up_deep_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_up_deep_graph_cases() {
    return TYPING_TEST_CASES.merge_up_deep_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_cases")
  public void merge_down_wide_graph(TypeB type1, TypeB type2, TypeB expected) {
    TYPING_TEST_CASES.merge_down_wide_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_down_wide_graph_cases() {
    return TYPING_TEST_CASES.merge_down_wide_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_cases")
  public void merge_down_deep_graph(TypeB type1, TypeB type2, Type expected) {
    TYPING_TEST_CASES.merge_down_deep_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_down_deep_graph_cases() {
    return TYPING_TEST_CASES.merge_down_deep_graph_cases();
  }
}
