package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.type.TestingTypeGraphS.buildGraph;
import static org.smoothbuild.testing.type.TestedAssignCasesS.TESTED_ASSIGN_CASES_S;
import static org.smoothbuild.testing.type.TestingTS.A;
import static org.smoothbuild.testing.type.TestingTS.ANY;
import static org.smoothbuild.testing.type.TestingTS.B;
import static org.smoothbuild.testing.type.TestingTS.BLOB;
import static org.smoothbuild.testing.type.TestingTS.BOOL;
import static org.smoothbuild.testing.type.TestingTS.INT;
import static org.smoothbuild.testing.type.TestingTS.NOTHING;
import static org.smoothbuild.testing.type.TestingTS.STRING;
import static org.smoothbuild.testing.type.TestingTS.join;
import static org.smoothbuild.testing.type.TestingTS.meet;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.type.TestedTSF;
import org.smoothbuild.testing.type.TestingTS;
import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;

public class TypingSTest {
  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_test_data")
  public void merge_up_wide_graph(MonoTS type1, MonoTS type2, MonoTS expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  private static Collection<Arguments> merge_up_wide_graph_test_data() {
    return buildWideGraph()
        .buildTestCases(nothing());
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_test_data")
  public void merge_up_deep_graph(MonoTS type1, MonoTS type2, MonoTS expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  private static Collection<Arguments> merge_up_deep_graph_test_data() {
    return buildGraph(list(blob()), 2, testingT())
        .buildTestCases(nothing());
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_test_data")
  public void merge_down_wide_graph(MonoTS type1, MonoTS type2, MonoTS expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  private static Collection<Arguments> merge_down_wide_graph_test_data() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(any());
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_test_data")
  public void merge_down_deep_graph(MonoTS type1, MonoTS type2, MonoTS expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  private static Collection<Arguments> merge_down_deep_graph_test_data() {
    return buildGraph(list(blob()), 2, testingT())
        .inverse()
        .buildTestCases(any());
  }

  private void testMergeBothWays(MonoTS type1, MonoTS type2, MonoTS expected, Side direction) {
    assertThat(typing().merge(type1, type2, direction))
        .isEqualTo(expected);
    assertThat(typing().merge(type2, type1, direction))
        .isEqualTo(expected);
  }

  private static TestingTypeGraphS buildWideGraph() {
    return buildGraph(testingT().typesForBuildWideGraph(), 1, testingT());
  }

  @ParameterizedTest
  @MethodSource("resolve_merges")
  public void resolve_merges(MonoTS type, MonoTS reduced) {
    assertThat(typing().resolveMerges(type))
        .isEqualTo(reduced);
  }

  public static List<Arguments> resolve_merges() {
    return list(
        arguments(STRING, STRING),
        arguments(A, A),
        arguments(f(INT, BLOB), f(INT, BLOB)),
        arguments(ar(INT), ar(INT)),
        arguments(ar(ar(INT)), ar(ar(INT))),

        arguments(join(BLOB, INT), ANY),
        arguments(join(A, B), ANY),

        arguments(join(ar(BLOB), ar(NOTHING)), ar(BLOB)),
        arguments(join(ar(ar(BLOB)), ar(NOTHING)), ar(ar(BLOB))),
        arguments(join(ar(BLOB), ar(ar(NOTHING))), ar(ANY)),
        arguments(join(ar(ar(BLOB)), ar(ar(NOTHING))), ar(ar(BLOB))),

        arguments(join(ar(BLOB), ar(ANY)), ar(ANY)),
        arguments(join(ar(ar(BLOB)), ar(ANY)), ar(ANY)),
        arguments(join(ar(BLOB), ar(ar(ANY))), ar(ANY)),
        arguments(join(ar(ar(BLOB)), ar(ar(ANY))), ar(ar(ANY))),

        arguments(meet(BLOB, INT), NOTHING),
        arguments(meet(A, B), NOTHING),

        arguments(meet(ar(BLOB), ar(NOTHING)), ar(NOTHING)),
        arguments(meet(ar(ar(BLOB)), ar(NOTHING)), ar(NOTHING)),
        arguments(meet(ar(BLOB), ar(ar(NOTHING))), ar(NOTHING)),
        arguments(meet(ar(ar(BLOB)), ar(ar(NOTHING))), ar(ar(NOTHING))),

        arguments(meet(ar(BLOB), ar(ANY)), ar(BLOB)),
        arguments(meet(ar(ar(BLOB)), ar(ANY)), ar(ar(BLOB))),
        arguments(meet(ar(BLOB), ar(ar(ANY))), ar(NOTHING)),
        arguments(meet(ar(ar(BLOB)), ar(ar(ANY))), ar(ar(BLOB))),

        arguments(join(f(INT, BLOB), f(INT, STRING)), f(INT, NOTHING)),
        arguments(join(f(INT, BLOB), f(BOOL, BLOB)), f(ANY, BLOB)),
        arguments(join(f(INT, BLOB), f(BLOB, INT)), f(ANY, NOTHING))
    );
  }

  private static MonoTS any() {
    return testingT().any();
  }

  private static MonoTS blob() {
    return testingT().blob();
  }

  private static MonoTS bool() {
    return testingT().bool();
  }

  private static MonoTS int_() {
    return testingT().int_();
  }

  private static MonoTS nothing() {
    return testingT().nothing();
  }

  private static MonoTS string() {
    return testingT().string();
  }

  private static MonoTS struct() {
    return testingT().struct();
  }

  private static MonoTS ar(MonoTS elemT) {
    return testingT().array(elemT);
  }

  private static MonoTS f(MonoTS resT, MonoTS... paramTs) {
    return testingT().func(resT, list(paramTs));
  }

  private static MonoTS f(MonoTS resT) {
    return f(resT, list());
  }

  private static MonoTS f(MonoTS resT, ImmutableList<MonoTS> paramTs) {
    return testingT().func(resT, paramTs);
  }

  private static TestingTS testingT() {
    return testedF().testingT();
  }

  private static TestedTSF testedF() {
    return TESTED_ASSIGN_CASES_S.testedTF();
  }

  private static TypeFS typeF() {
    return typing().typeF();
  }

  private static TypingS typing() {
    return TESTED_ASSIGN_CASES_S.testingT().typing();
  }
}
