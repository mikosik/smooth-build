package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.TestedAssignCases.INSTANCE_H;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Type;

public class TypingHTest {
  private final static TypingTestCases<TypeH, TestedTH> TYPING_TEST_CASES =
      new TypingTestCases<>(INSTANCE_H);

  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void testContains(TypeH type, TypeH contained, boolean expected) {
    TYPING_TEST_CASES.testContains(type, contained, expected);
  }

  public static List<Arguments> contains_test_data() {
    return TYPING_TEST_CASES.contains_test_data();
  }

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignSpec<TestedTH> spec) {
    TYPING_TEST_CASES.isAssignable(spec);
  }

  public static List<? extends TestedAssignSpec<TestedTH>> isAssignable_test_data() {
    return TYPING_TEST_CASES.isAssignable_test_data();
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignSpec<TestedTH> spec) {
    TYPING_TEST_CASES.isParamAssignable(spec);
  }

  public static List<? extends TestedAssignSpec<TestedTH>> isParamAssignable_test_data() {
    return TYPING_TEST_CASES.isParamAssignable_test_data();
  }

  @ParameterizedTest
  @MethodSource("inferVarBounds_test_data")
  public void inferVarBounds(TypeH type, TypeH assigned, BoundsMap<TypeH> expected) {
    TYPING_TEST_CASES.inferVarBounds(type, assigned, expected);
  }

  public static List<Arguments> inferVarBounds_test_data() {
    return TYPING_TEST_CASES.inferVarBounds_test_data();
  }

  @ParameterizedTest
  @MethodSource("mapVars_test_data")
  public void mapVars(TypeH type, BoundsMap<TypeH> boundsMap, TypeH expected) {
    TYPING_TEST_CASES.mapVars(type, boundsMap, expected);
  }

  public static List<Arguments> mapVars_test_data() {
    return TYPING_TEST_CASES.mapVars_test_data();
  }

  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_cases")
  public void merge_up_wide_graph(TypeH type1, TypeH type2, TypeH expected) {
    TYPING_TEST_CASES.merge_up_wide_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_up_wide_graph_cases() {
    return TYPING_TEST_CASES.merge_up_wide_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_cases")
  public void merge_up_deep_graph(TypeH type1, TypeH type2, TypeH expected) {
    TYPING_TEST_CASES.merge_up_deep_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_up_deep_graph_cases() {
    return TYPING_TEST_CASES.merge_up_deep_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_cases")
  public void merge_down_wide_graph(TypeH type1, TypeH type2, TypeH expected) {
    TYPING_TEST_CASES.merge_down_wide_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_down_wide_graph_cases() {
    return TYPING_TEST_CASES.merge_down_wide_graph_cases();
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_cases")
  public void merge_down_deep_graph(TypeH type1, TypeH type2, Type expected) {
    TYPING_TEST_CASES.merge_down_deep_graph(type1, type2, expected);
  }

  public static Collection<Arguments> merge_down_deep_graph_cases() {
    return TYPING_TEST_CASES.merge_down_deep_graph_cases();
  }
}
