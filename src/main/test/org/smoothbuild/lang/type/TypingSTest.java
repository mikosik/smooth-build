package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.type.TestingTypeGraphS.buildGraph;
import static org.smoothbuild.lang.type.VarBoundsS.varBoundsS;
import static org.smoothbuild.lang.type.VarSetS.varSetS;
import static org.smoothbuild.testing.type.TestedAssignCasesS.TESTED_ASSIGN_CASES_S;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.type.Side.LOWER;
import static org.smoothbuild.util.type.Side.UPPER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.type.TestedAssignSpecS;
import org.smoothbuild.testing.type.TestedTSF;
import org.smoothbuild.testing.type.TestingTS;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TypingSTest {
  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void testContains(TypeS type, TypeS contained, boolean expected) {
    assertThat(typing().contains(type, contained))
        .isEqualTo(expected);
  }

  private static List<Arguments> contains_test_data() {
    ArrayList<Arguments> result = new ArrayList<>();

    result.add(arguments(any(), any(), true));
    result.add(arguments(any(), blob(), false));
    result.add(arguments(any(), bool(), false));
    result.add(arguments(any(), int_(), false));
    result.add(arguments(any(), nothing(), false));
    result.add(arguments(any(), string(), false));
    result.add(arguments(any(), struct(), false));

    result.add(arguments(any(), ar(any()), false));
    result.add(arguments(any(), ar(blob()), false));
    result.add(arguments(any(), ar(bool()), false));
    result.add(arguments(any(), ar(int_()), false));
    result.add(arguments(any(), ar(nothing()), false));
    result.add(arguments(any(), ar(string()), false));
    result.add(arguments(any(), ar(struct()), false));

    result.add(arguments(any(), f(any()), false));
    result.add(arguments(any(), f(blob()), false));
    result.add(arguments(any(), f(bool()), false));
    result.add(arguments(any(), f(int_()), false));
    result.add(arguments(any(), f(nothing()), false));
    result.add(arguments(any(), f(string()), false));
    result.add(arguments(any(), f(struct()), false));

    result.add(arguments(blob(), any(), false));
    result.add(arguments(blob(), blob(), true));
    result.add(arguments(blob(), bool(), false));
    result.add(arguments(blob(), int_(), false));
    result.add(arguments(blob(), nothing(), false));
    result.add(arguments(blob(), string(), false));
    result.add(arguments(blob(), struct(), false));

    result.add(arguments(blob(), ar(any()), false));
    result.add(arguments(blob(), ar(blob()), false));
    result.add(arguments(blob(), ar(bool()), false));
    result.add(arguments(blob(), ar(int_()), false));
    result.add(arguments(blob(), ar(nothing()), false));
    result.add(arguments(blob(), ar(string()), false));
    result.add(arguments(blob(), ar(struct()), false));

    result.add(arguments(blob(), f(any()), false));
    result.add(arguments(blob(), f(blob()), false));
    result.add(arguments(blob(), f(bool()), false));
    result.add(arguments(blob(), f(int_()), false));
    result.add(arguments(blob(), f(nothing()), false));
    result.add(arguments(blob(), f(string()), false));
    result.add(arguments(blob(), f(struct()), false));

    result.add(arguments(ar(blob()), any(), false));
    result.add(arguments(ar(blob()), blob(), true));
    result.add(arguments(ar(blob()), bool(), false));
    result.add(arguments(ar(blob()), int_(), false));
    result.add(arguments(ar(blob()), nothing(), false));
    result.add(arguments(ar(blob()), string(), false));
    result.add(arguments(ar(blob()), struct(), false));

    result.add(arguments(ar(blob()), ar(any()), false));
    result.add(arguments(ar(blob()), ar(blob()), true));
    result.add(arguments(ar(blob()), ar(bool()), false));
    result.add(arguments(ar(blob()), ar(int_()), false));
    result.add(arguments(ar(blob()), ar(nothing()), false));
    result.add(arguments(ar(blob()), ar(string()), false));
    result.add(arguments(ar(blob()), ar(struct()), false));

    result.add(arguments(ar(blob()), f(any()), false));
    result.add(arguments(ar(blob()), f(blob()), false));
    result.add(arguments(ar(blob()), f(bool()), false));
    result.add(arguments(ar(blob()), f(int_()), false));
    result.add(arguments(ar(blob()), f(nothing()), false));
    result.add(arguments(ar(blob()), f(string()), false));
    result.add(arguments(ar(blob()), f(struct()), false));

    result.add(arguments(f(blob()), any(), false));
    result.add(arguments(f(blob()), blob(), true));
    result.add(arguments(f(blob()), bool(), false));
    result.add(arguments(f(blob()), int_(), false));
    result.add(arguments(f(blob()), nothing(), false));
    result.add(arguments(f(blob()), string(), false));
    result.add(arguments(f(blob()), struct(), false));

    result.add(arguments(f(blob()), ar(any()), false));
    result.add(arguments(f(blob()), ar(blob()), false));
    result.add(arguments(f(blob()), ar(bool()), false));
    result.add(arguments(f(blob()), ar(int_()), false));
    result.add(arguments(f(blob()), ar(nothing()), false));
    result.add(arguments(f(blob()), ar(string()), false));
    result.add(arguments(f(blob()), ar(struct()), false));

    result.add(arguments(f(blob()), f(any()), false));
    result.add(arguments(f(blob()), f(blob()), true));
    result.add(arguments(f(blob()), f(bool()), false));
    result.add(arguments(f(blob()), f(int_()), false));
    result.add(arguments(f(blob()), f(nothing()), false));
    result.add(arguments(f(blob()), f(string()), false));
    result.add(arguments(f(blob()), f(struct()), false));

    result.add(arguments(f(string(), blob()), any(), false));
    result.add(arguments(f(string(), blob()), blob(), true));
    result.add(arguments(f(string(), blob()), bool(), false));
    result.add(arguments(f(string(), blob()), int_(), false));
    result.add(arguments(f(string(), blob()), nothing(), false));
    result.add(arguments(f(string(), blob()), string(), true));
    result.add(arguments(f(string(), blob()), struct(), false));

    result.add(arguments(f(string(), blob()), ar(any()), false));
    result.add(arguments(f(string(), blob()), ar(blob()), false));
    result.add(arguments(f(string(), blob()), ar(bool()), false));
    result.add(arguments(f(string(), blob()), ar(int_()), false));
    result.add(arguments(f(string(), blob()), ar(nothing()), false));
    result.add(arguments(f(string(), blob()), ar(string()), false));
    result.add(arguments(f(string(), blob()), ar(struct()), false));

    result.add(arguments(f(string(), blob()), f(any()), false));
    result.add(arguments(f(string(), blob()), f(blob()), false));
    result.add(arguments(f(string(), blob()), f(bool()), false));
    result.add(arguments(f(string(), blob()), f(int_()), false));
    result.add(arguments(f(string(), blob()), f(nothing()), false));
    result.add(arguments(f(string(), blob()), f(string()), false));
    result.add(arguments(f(string(), blob()), f(struct()), false));

    result.add(arguments(f(string(), blob()), f(string(), blob()), true));
    return result;
  }

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignSpecS spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(typing().isAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  private static List<? extends TestedAssignSpecS> isAssignable_test_data() {
    return TESTED_ASSIGN_CASES_S.assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignSpecS spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(typing().isParamAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  private static List<? extends TestedAssignSpecS> isParamAssignable_test_data() {
    return TESTED_ASSIGN_CASES_S.param_assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("inferVarBounds_test_data")
  public void inferVarBounds(TypeS type, TypeS assigned, VarBoundsS expected) {
    assertThat(typing().inferVarBounds(type, assigned, LOWER))
        .isEqualTo(expected);
  }

  private static List<Arguments> inferVarBounds_test_data() {
    var r = new ArrayList<Arguments>();
    for (TypeS type : concat(testingT().elementaryTypes(), x())) {
      if (type instanceof NothingTS) {
        r.add(arguments(a(), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(a(), ar(nothing()), vb(a(), LOWER, ar(nothing()))));
        r.add(arguments(a(), ar(ar(nothing())), vb(a(), LOWER, ar(ar(nothing())))));

        // arrays
        r.add(arguments(ar(a()), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(ar(a()), ar(nothing()), vb(a(), LOWER, nothing())));
        r.add(arguments(ar(a()), ar(ar(nothing())), vb(a(), LOWER, ar(nothing()))));

        r.add(arguments(ar(ar(a())), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(ar(ar(a())), ar(nothing()), vb(a(), LOWER, nothing())));
        r.add(arguments(ar(ar(a())), ar(ar(nothing())), vb(a(), LOWER, nothing())));

        // funcs
        r.add(arguments(f(a()), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(f(f(a())), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(f(f(f(a()))), nothing(), vb(a(), LOWER, nothing())));

        r.add(arguments(f(bool(), a()), nothing(), vb(a(), UPPER, any())));
        r.add(arguments(f(bool(), f(a())), nothing(), vb(a(), UPPER, any())));
        r.add(arguments(f(bool(), f(f(a()))), nothing(), vb(a(), UPPER, any())));

        r.add(arguments(f(bool(), f(blob(), a())), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(f(bool(), f(blob(), f(a()))), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(f(bool(), f(blob(), f(f(a())))), nothing(), vb(a(), LOWER, nothing())));

        // arrays + funcs
        r.add(arguments(ar(f(a())), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(ar(f(string(), a())), nothing(), vb(a(), UPPER, any())));

        r.add(arguments(f(ar(a())), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(f(bool(), ar(a())), nothing(), vb(a(), UPPER, any())));
      } else {
        r.add(arguments(a(), type, vb(a(), LOWER, type)));
        r.add(arguments(a(), ar(type), vb(a(), LOWER, ar(type))));
        r.add(arguments(a(), ar(ar(type)), vb(a(), LOWER, ar(ar(type)))));

        // arrays
        r.add(arguments(ar(a()), type, vb()));
        r.add(arguments(ar(a()), ar(type), vb(a(), LOWER, type)));
        r.add(arguments(ar(a()), ar(ar(type)), vb(a(), LOWER, ar(type))));

        r.add(arguments(ar(ar(a())), type, vb()));
        r.add(arguments(ar(ar(a())), ar(type), vb()));
        r.add(arguments(ar(ar(a())), ar(ar(type)), vb(a(), LOWER, type)));

        // funcs
        r.add(arguments(f(a()), type, vb()));
        r.add(arguments(f(a()), f(type), vb(a(), LOWER, type)));
        r.add(arguments(f(a()), f(f(type)), vb(a(), LOWER, f(type))));
        r.add(arguments(f(a()), f(f(f(type))), vb(a(), LOWER, f(f(type)))));

        r.add(arguments(f(f(a())), type, vb()));
        r.add(arguments(f(f(a())), f(type), vb()));
        r.add(arguments(f(f(a())), f(f(type)), vb(a(), LOWER, type)));
        r.add(arguments(f(f(a())), f(f(f(type))), vb(a(), LOWER, f(type))));

        r.add(arguments(f(f(f(a()))), type, vb()));
        r.add(arguments(f(f(f(a()))), f(type), vb()));
        r.add(arguments(f(f(f(a()))), f(f(type)), vb()));
        r.add(arguments(f(f(f(a()))), f(f(f(type))), vb(a(), LOWER, type)));

        r.add(arguments(f(bool(), a()), f(bool(), type), vb(a(), UPPER, type)));
        r.add(arguments(f(bool(), f(a())), f(bool(), f(type)), vb(a(), UPPER, type)));
        r.add(arguments(f(bool(), f(f(a()))), f(bool(), f(f(type))), vb(a(), UPPER, type)));

        r.add(arguments(f(bool(), f(blob(), a())), f(bool(), f(blob(), type)), vb(a(), LOWER, type)));
        r.add(arguments(f(bool(), f(blob(), f(a()))), f(bool(), f(blob(), f(type))), vb(a(),
            LOWER, type)));
        r.add(arguments(f(bool(), f(blob(), f(f(a())))), f(bool(), f(blob(), f(f(type)))), vb(a(),
            LOWER, type)));

        // arrays + funcs
        r.add(arguments(ar(f(a())), ar(f(type)), vb(a(), LOWER, type)));
        r.add(arguments(ar(f(bool(), a())), ar(f(bool(), type)), vb(a(), UPPER, type)));

        r.add(arguments(f(ar(a())), f(ar(type)), vb(a(), LOWER, type)));
        r.add(arguments(f(bool(), ar(a())), f(bool(), ar(type)), vb(a(), UPPER, type)));
      }
    }
    return r;
  }

  @ParameterizedTest
  @MethodSource("mapVarsLower_test_data")
  public void mapVarsLower(TypeS type, VarBoundsS varBounds, TypeS expected) {
    assertThat(typing().mapVarsLower(type, varBounds))
        .isEqualTo(expected);
  }

  private static List<Arguments> mapVarsLower_test_data() {
    var r = new ArrayList<Arguments>();
    // types without variables
    r.add(arguments(int_(), vb(x(), LOWER, x()), int_()));
    r.add(arguments(ar(int_()), vb(x(), LOWER, x()), ar(int_())));
    r.add(arguments(struct(), vb(x(), LOWER, x()), struct()));

    // `X`
    r.add(arguments(x(), vb(x(), LOWER, x()), x()));
    r.add(arguments(x(), vb(x(), LOWER, a()), a()));
    r.add(arguments(x(), vb(x(), LOWER, ar(a())), ar(a())));
    r.add(arguments(x(), vb(x(), LOWER, struct()), struct()));

    // array[X]
    r.add(arguments(ar(x()), vb(x(), LOWER, a()), ar(a())));
    r.add(arguments(ar(x()), vb(x(), LOWER, ar(a())), ar(ar(a()))));
    r.add(arguments(ar(x()), vb(x(), LOWER, struct()), ar(struct())));

    // {X}

    // X(Y)
    r.add(arguments(f(x(), y()), vb(x(), LOWER, a(), y(), UPPER, b()), f(a(), b())));

    return r;
  }

  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_test_data")
  public void merge_up_wide_graph(TypeS type1, TypeS type2, TypeS expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  private static Collection<Arguments> merge_up_wide_graph_test_data() {
    return buildWideGraph()
        .buildTestCases(nothing());
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_test_data")
  public void merge_up_deep_graph(TypeS type1, TypeS type2, TypeS expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  private static Collection<Arguments> merge_up_deep_graph_test_data() {
    return buildGraph(list(blob()), 2, testingT())
        .buildTestCases(nothing());
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_test_data")
  public void merge_down_wide_graph(TypeS type1, TypeS type2, TypeS expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  private static Collection<Arguments> merge_down_wide_graph_test_data() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(any());
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_test_data")
  public void merge_down_deep_graph(TypeS type1, TypeS type2, TypeS expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  private static Collection<Arguments> merge_down_deep_graph_test_data() {
    return buildGraph(list(blob()), 2, testingT())
        .inverse()
        .buildTestCases(any());
  }

  private void testMergeBothWays(TypeS type1, TypeS type2, TypeS expected, Side direction) {
    assertThat(typing().merge(type1, type2, direction))
        .isEqualTo(expected);
    assertThat(typing().merge(type2, type1, direction))
        .isEqualTo(expected);
  }

  private static TestingTypeGraphS buildWideGraph() {
    return buildGraph(testingT().typesForBuildWideGraph(), 1, testingT());
  }

  private static Bounds<TypeS> oneSideBound(Side side, TypeS type) {
    return typeF().oneSideBound(side, type);
  }

  private static VarBoundsS vb(
      VarS var1, Side side1, VarS bound1,
      VarS var2, Side side2, VarS bound2) {
    var bounds1 = oneSideBound(side1, bound1);
    var bounds2 = oneSideBound(side2, bound2);
    if (var1.equals(var2)) {
      return varBoundsS(new BoundedS(var1, typing().merge(bounds1, bounds2)));
    } else {
      return new VarBoundsS(ImmutableMap.of(
          var1, new BoundedS(var1, bounds1),
          var2, new BoundedS(var2, bounds2)
      ));
    }
  }

  private static VarBoundsS vb(VarS var, Side side, TypeS bound) {
    return varBoundsS(new BoundedS(var, oneSideBound(side, bound)));
  }

  private static VarBoundsS vb() {
    return varBoundsS();
  }

  private static VarS a() {
    return testingT().varA();
  }

  private static VarS b() {
    return testingT().varB();
  }

  private static VarS x() {
    return testingT().varX();
  }

  private static VarS y() {
    return testingT().varY();
  }

  private static VarSetS vs(VarS... elements) {
    return varSetS(elements);
  }

  private static TypeS any() {
    return testingT().any();
  }

  private static TypeS blob() {
    return testingT().blob();
  }

  private static TypeS bool() {
    return testingT().bool();
  }

  private static TypeS int_() {
    return testingT().int_();
  }

  private static TypeS nothing() {
    return testingT().nothing();
  }

  private static TypeS string() {
    return testingT().string();
  }

  private static TypeS struct() {
    return testingT().struct();
  }

  private static TypeS ar(TypeS elemT) {
    return testingT().array(elemT);
  }

  private static TypeS f(TypeS resT, TypeS... paramTs) {
    return testingT().func(resT, list(paramTs));
  }

  private static TypeS f(TypeS resT) {
    return f(resT, list());
  }

  private static TypeS f(TypeS resT, ImmutableList<TypeS> paramTs) {
    return testingT().func(resT, paramTs);
  }

  private static TestingTS testingT() {
    return testedF().testingT();
  }

  private static TestedTSF testedF() {
    return TESTED_ASSIGN_CASES_S.testedTF();
  }

  private static TypeSF typeF() {
    return typing().typeF();
  }

  private static TypingS typing() {
    return TESTED_ASSIGN_CASES_S.testingT().typing();
  }
}
