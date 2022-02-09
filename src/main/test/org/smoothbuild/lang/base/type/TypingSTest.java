package org.smoothbuild.lang.base.type;

import static org.smoothbuild.testing.type.TestedAssignCases.INSTANCE_S;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.VarBounds;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.testing.type.TestedAssignSpec;
import org.smoothbuild.testing.type.TestedTS;

public class TypingSTest {
  private final static TypingTestCases<TypeS, TestedTS> TYPING_TEST_CASES =
      new TypingTestCases<>(INSTANCE_S);

  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void testContains(TypeS type, TypeS contained, boolean expected) {
    TYPING_TEST_CASES.testContains(type, contained, expected);
  }

  public static List<Arguments> contains_test_data() {
    return TYPING_TEST_CASES.contains_test_data();
  }

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignSpec<TestedTS> spec) {
    TYPING_TEST_CASES.isAssignable(spec);
  }

  public static List<? extends TestedAssignSpec<TestedTS>> isAssignable_test_data() {
    return TYPING_TEST_CASES.isAssignable_test_data();
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignSpec<TestedTS> spec) {
    TYPING_TEST_CASES.isParamAssignable(spec);
  }

  public static List<? extends TestedAssignSpec<TestedTS>> isParamAssignable_test_data() {
    return TYPING_TEST_CASES.isParamAssignable_test_data();
  }

  @ParameterizedTest
  @MethodSource("inferVarBounds_test_data")
  public void inferVarBounds(TypeS type, TypeS assigned, VarBounds<TypeS> expected) {
    TYPING_TEST_CASES.inferVarBounds(type, assigned, expected);
  }

  public static List<Arguments> inferVarBounds_test_data() {
    return TYPING_TEST_CASES.inferVarBounds_test_data();
  }

  @ParameterizedTest
  @MethodSource("mapVars_test_data")
  public void mapVars(TypeS type, VarBounds<TypeS> varBounds, TypeS expected) {
    TYPING_TEST_CASES.mapVars(type, varBounds, expected);
  }

  public static List<Arguments> mapVars_test_data() {
    return TYPING_TEST_CASES.mapVars_test_data();
  }

  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_cases")
  public void merge_up_wide_graph(TypeS type1, TypeS type2, TypeS expected) {
    TYPING_TEST_CASES.merge_up_wide_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_up_wide_graph_cases() {
    return TYPING_TEST_CASES.merge_up_wide_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_cases")
  public void merge_up_deep_graph(TypeS type1, TypeS type2, TypeS expected) {
    TYPING_TEST_CASES.merge_up_deep_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_up_deep_graph_cases() {
    return TYPING_TEST_CASES.merge_up_deep_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_cases")
  public void merge_down_wide_graph(TypeS type1, TypeS type2, TypeS expected) {
    TYPING_TEST_CASES.merge_down_wide_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_down_wide_graph_cases() {
    return TYPING_TEST_CASES.merge_down_wide_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_cases")
  public void merge_down_deep_graph(TypeS type1, TypeS type2, Type expected) {
    TYPING_TEST_CASES.merge_down_deep_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_down_deep_graph_cases() {
    return TYPING_TEST_CASES.merge_down_deep_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("open_cases")
  public void open(TypeS type, TypeS expected) {
    TYPING_TEST_CASES.openVars(type, expected);
  }

  public static Collection<Arguments> open_cases() {
    return TYPING_TEST_CASES.openVars_test_data();
  }

  @ParameterizedTest
  @MethodSource("close_cases")
  public void close(TypeS type, TypeS expected) {
    TYPING_TEST_CASES.closeVars(type, expected);
  }

  public static Collection<Arguments> close_cases() {
    return TYPING_TEST_CASES.closeVars_test_data();
  }
}
