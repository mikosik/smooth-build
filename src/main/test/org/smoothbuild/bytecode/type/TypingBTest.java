package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.bytecode.type.TestingTypeGraphB.buildGraph;
import static org.smoothbuild.testing.type.TestedAssignCasesB.TESTED_ASSIGN_CASES_B;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.Collection;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.testing.type.TestedTBF;
import org.smoothbuild.testing.type.TestingTB;
import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;

public class TypingBTest {

  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_test_data")
  public void merge_up_wide_graph(TypeB type1, TypeB type2, TypeB expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  private static Collection<Arguments> merge_up_wide_graph_test_data() {
    return buildWideGraph()
        .buildTestCases(nothing());
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_test_data")
  public void merge_up_deep_graph(TypeB type1, TypeB type2, TypeB expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  private static Collection<Arguments> merge_up_deep_graph_test_data() {
    return buildGraph(list(blob()), 2, testingT())
        .buildTestCases(nothing());
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_test_data")
  public void merge_down_wide_graph(TypeB type1, TypeB type2, TypeB expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  private static Collection<Arguments> merge_down_wide_graph_test_data() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(any());
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_test_data")
  public void merge_down_deep_graph(TypeB type1, TypeB type2, TypeB expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  private static Collection<Arguments> merge_down_deep_graph_test_data() {
    return buildGraph(list(blob()), 2, testingT())
        .inverse()
        .buildTestCases(any());
  }

  private void testMergeBothWays(TypeB type1, TypeB type2, TypeB expected, Side direction) {
    assertThat(typing().merge(type1, type2, direction))
        .isEqualTo(expected);
    assertThat(typing().merge(type2, type1, direction))
        .isEqualTo(expected);
  }

  private static TestingTypeGraphB buildWideGraph() {
    return buildGraph(testingT().typesForBuildWideGraph(), 1, testingT());
  }

  private static TypeB any() {
    return testingT().any();
  }

  private static TypeB blob() {
    return testingT().blob();
  }

  private static TypeB bool() {
    return testingT().bool();
  }

  private static TypeB int_() {
    return testingT().int_();
  }

  private static TypeB nothing() {
    return testingT().nothing();
  }

  private static TypeB string() {
    return testingT().string();
  }

  private static TypeB tuple() {
    return testingT().tuple();
  }

  private static TypeB tuple(TypeB item) {
    return testingT().tuple(list(item));
  }

  private static TypeB ar(TypeB elemT) {
    return testingT().array(elemT);
  }

  private static TypeB f(TypeB resT, TypeB... paramTs) {
    return testingT().func(resT, list(paramTs));
  }

  private static TypeB f(TypeB resT) {
    return f(resT, list());
  }

  private static TypeB f(TypeB resT, ImmutableList<TypeB> paramTs) {
    return testingT().func(resT, paramTs);
  }

  private static TestingTB testingT() {
    return testedF().testingT();
  }

  private static TestedTBF testedF() {
    return TESTED_ASSIGN_CASES_B.testedTF();
  }

  private static TypeFB typeF() {
    return typing().typeF();
  }

  private static TypingB typing() {
    return TESTED_ASSIGN_CASES_B.testingT().typing();
  }
}
