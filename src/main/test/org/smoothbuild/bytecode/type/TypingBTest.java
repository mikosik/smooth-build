package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.bytecode.type.TestingTypeGraphB.buildGraph;
import static org.smoothbuild.bytecode.type.val.VarBoundsB.varBoundsB;
import static org.smoothbuild.bytecode.type.val.VarSetB.varSetB;
import static org.smoothbuild.testing.type.TestedAssignCasesB.TESTED_ASSIGN_CASES_B;
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
import org.smoothbuild.bytecode.type.val.BoundedB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.bytecode.type.val.VarBoundsB;
import org.smoothbuild.bytecode.type.val.VarSetB;
import org.smoothbuild.testing.type.TestedAssignSpecB;
import org.smoothbuild.testing.type.TestedTBF;
import org.smoothbuild.testing.type.TestingTB;
import org.smoothbuild.util.type.Side;
import org.smoothbuild.util.type.Sides;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TypingBTest {

  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void testContains(TypeB type, TypeB contained, boolean expected) {
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
    result.add(arguments(any(), tuple(), false));

    result.add(arguments(any(), ar(any()), false));
    result.add(arguments(any(), ar(blob()), false));
    result.add(arguments(any(), ar(bool()), false));
    result.add(arguments(any(), ar(int_()), false));
    result.add(arguments(any(), ar(nothing()), false));
    result.add(arguments(any(), ar(string()), false));
    result.add(arguments(any(), ar(tuple()), false));

    result.add(arguments(any(), f(any()), false));
    result.add(arguments(any(), f(blob()), false));
    result.add(arguments(any(), f(bool()), false));
    result.add(arguments(any(), f(int_()), false));
    result.add(arguments(any(), f(nothing()), false));
    result.add(arguments(any(), f(string()), false));
    result.add(arguments(any(), f(tuple()), false));

    result.add(arguments(blob(), any(), false));
    result.add(arguments(blob(), blob(), true));
    result.add(arguments(blob(), bool(), false));
    result.add(arguments(blob(), int_(), false));
    result.add(arguments(blob(), nothing(), false));
    result.add(arguments(blob(), string(), false));
    result.add(arguments(blob(), tuple(), false));

    result.add(arguments(blob(), ar(any()), false));
    result.add(arguments(blob(), ar(blob()), false));
    result.add(arguments(blob(), ar(bool()), false));
    result.add(arguments(blob(), ar(int_()), false));
    result.add(arguments(blob(), ar(nothing()), false));
    result.add(arguments(blob(), ar(string()), false));
    result.add(arguments(blob(), ar(tuple()), false));

    result.add(arguments(blob(), f(any()), false));
    result.add(arguments(blob(), f(blob()), false));
    result.add(arguments(blob(), f(bool()), false));
    result.add(arguments(blob(), f(int_()), false));
    result.add(arguments(blob(), f(nothing()), false));
    result.add(arguments(blob(), f(string()), false));
    result.add(arguments(blob(), f(tuple()), false));

    result.add(arguments(ar(blob()), any(), false));
    result.add(arguments(ar(blob()), blob(), true));
    result.add(arguments(ar(blob()), bool(), false));
    result.add(arguments(ar(blob()), int_(), false));
    result.add(arguments(ar(blob()), nothing(), false));
    result.add(arguments(ar(blob()), string(), false));
    result.add(arguments(ar(blob()), tuple(), false));

    result.add(arguments(ar(blob()), ar(any()), false));
    result.add(arguments(ar(blob()), ar(blob()), true));
    result.add(arguments(ar(blob()), ar(bool()), false));
    result.add(arguments(ar(blob()), ar(int_()), false));
    result.add(arguments(ar(blob()), ar(nothing()), false));
    result.add(arguments(ar(blob()), ar(string()), false));
    result.add(arguments(ar(blob()), ar(tuple()), false));

    result.add(arguments(ar(blob()), f(any()), false));
    result.add(arguments(ar(blob()), f(blob()), false));
    result.add(arguments(ar(blob()), f(bool()), false));
    result.add(arguments(ar(blob()), f(int_()), false));
    result.add(arguments(ar(blob()), f(nothing()), false));
    result.add(arguments(ar(blob()), f(string()), false));
    result.add(arguments(ar(blob()), f(tuple()), false));

    result.add(arguments(tuple(blob()), any(), false));
    result.add(arguments(tuple(blob()), blob(), true));
    result.add(arguments(tuple(blob()), bool(), false));
    result.add(arguments(tuple(blob()), int_(), false));
    result.add(arguments(tuple(blob()), nothing(), false));
    result.add(arguments(tuple(blob()), string(), false));

    result.add(arguments(tuple(blob()), tuple(any()), false));
    result.add(arguments(tuple(blob()), tuple(blob()), true));
    result.add(arguments(tuple(blob()), tuple(bool()), false));
    result.add(arguments(tuple(blob()), tuple(int_()), false));
    result.add(arguments(tuple(blob()), tuple(nothing()), false));
    result.add(arguments(tuple(blob()), tuple(string()), false));

    result.add(arguments(tuple(blob()), ar(any()), false));
    result.add(arguments(tuple(blob()), ar(blob()), false));
    result.add(arguments(tuple(blob()), ar(bool()), false));
    result.add(arguments(tuple(blob()), ar(int_()), false));
    result.add(arguments(tuple(blob()), ar(nothing()), false));
    result.add(arguments(tuple(blob()), ar(string()), false));
    result.add(arguments(tuple(blob()), ar(tuple()), false));

    result.add(arguments(tuple(blob()), f(any()), false));
    result.add(arguments(tuple(blob()), f(blob()), false));
    result.add(arguments(tuple(blob()), f(bool()), false));
    result.add(arguments(tuple(blob()), f(int_()), false));
    result.add(arguments(tuple(blob()), f(nothing()), false));
    result.add(arguments(tuple(blob()), f(string()), false));
    result.add(arguments(tuple(blob()), f(tuple()), false));

    result.add(arguments(f(blob()), any(), false));
    result.add(arguments(f(blob()), blob(), true));
    result.add(arguments(f(blob()), bool(), false));
    result.add(arguments(f(blob()), int_(), false));
    result.add(arguments(f(blob()), nothing(), false));
    result.add(arguments(f(blob()), string(), false));
    result.add(arguments(f(blob()), tuple(), false));

    result.add(arguments(f(blob()), ar(any()), false));
    result.add(arguments(f(blob()), ar(blob()), false));
    result.add(arguments(f(blob()), ar(bool()), false));
    result.add(arguments(f(blob()), ar(int_()), false));
    result.add(arguments(f(blob()), ar(nothing()), false));
    result.add(arguments(f(blob()), ar(string()), false));
    result.add(arguments(f(blob()), ar(tuple()), false));

    result.add(arguments(f(blob()), f(any()), false));
    result.add(arguments(f(blob()), f(blob()), true));
    result.add(arguments(f(blob()), f(bool()), false));
    result.add(arguments(f(blob()), f(int_()), false));
    result.add(arguments(f(blob()), f(nothing()), false));
    result.add(arguments(f(blob()), f(string()), false));
    result.add(arguments(f(blob()), f(tuple()), false));

    result.add(arguments(f(string(), blob()), any(), false));
    result.add(arguments(f(string(), blob()), blob(), true));
    result.add(arguments(f(string(), blob()), bool(), false));
    result.add(arguments(f(string(), blob()), int_(), false));
    result.add(arguments(f(string(), blob()), nothing(), false));
    result.add(arguments(f(string(), blob()), string(), true));
    result.add(arguments(f(string(), blob()), tuple(), false));

    result.add(arguments(f(string(), blob()), ar(any()), false));
    result.add(arguments(f(string(), blob()), ar(blob()), false));
    result.add(arguments(f(string(), blob()), ar(bool()), false));
    result.add(arguments(f(string(), blob()), ar(int_()), false));
    result.add(arguments(f(string(), blob()), ar(nothing()), false));
    result.add(arguments(f(string(), blob()), ar(string()), false));
    result.add(arguments(f(string(), blob()), ar(tuple()), false));

    result.add(arguments(f(string(), blob()), f(any()), false));
    result.add(arguments(f(string(), blob()), f(blob()), false));
    result.add(arguments(f(string(), blob()), f(bool()), false));
    result.add(arguments(f(string(), blob()), f(int_()), false));
    result.add(arguments(f(string(), blob()), f(nothing()), false));
    result.add(arguments(f(string(), blob()), f(string()), false));
    result.add(arguments(f(string(), blob()), f(tuple()), false));

    result.add(arguments(f(string(), blob()), f(string(), blob()), true));
    return result;
  }

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignSpecB spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(typing().isAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  private static List<? extends TestedAssignSpecB> isAssignable_test_data() {
    return TESTED_ASSIGN_CASES_B.assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignSpecB spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(typing().isParamAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  private static List<? extends TestedAssignSpecB> isParamAssignable_test_data() {
    return TESTED_ASSIGN_CASES_B.param_assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("inferVarBounds_test_data")
  public void inferVarBounds(TypeB type, TypeB assigned, VarBoundsB expected) {
    assertThat(typing().inferVarBounds(type, assigned, LOWER))
        .isEqualTo(expected);
  }

  private static List<Arguments> inferVarBounds_test_data() {
    var r = new ArrayList<Arguments>();
    for (TypeB type : concat(testingT().elementaryTypes(), x())) {
      if (type instanceof NothingTB) {
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

        // tuples
        r.add(arguments(tuple(a()), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(tuple(a()), tuple(nothing()), vb(a(), LOWER, nothing())));
        r.add(arguments(tuple(a()), tuple(tuple(nothing())), vb(a(), LOWER, tuple(nothing()))));

        r.add(arguments(tuple(tuple(a())), nothing(), vb(a(), LOWER, nothing())));
        r.add(arguments(tuple(tuple(a())), tuple(nothing()), vb(a(), LOWER, nothing())));
        r.add(arguments(tuple(tuple(a())), tuple(tuple(nothing())), vb(a(), LOWER, nothing())));

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

        // tuples
        r.add(arguments(tuple(a()), type, vb()));
        r.add(arguments(tuple(a()), tuple(type), vb(a(), LOWER, type)));
        r.add(arguments(tuple(a()), tuple(tuple(type)), vb(a(), LOWER, tuple(type))));

        r.add(arguments(tuple(tuple(a())), type, vb()));
        r.add(arguments(tuple(tuple(a())), tuple(type), vb()));
        r.add(arguments(tuple(tuple(a())), tuple(tuple(type)), vb(a(), LOWER, type)));

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
  public void mapVarsLower(TypeB type, VarBoundsB varBounds, TypeB expected) {
    assertThat(typing().mapVarsLower(type, varBounds))
        .isEqualTo(expected);
  }

  private static List<Arguments> mapVarsLower_test_data() {
    var r = new ArrayList<Arguments>();
    // types without variables
    r.add(arguments(int_(), vb(x(), LOWER, x()), int_()));
    r.add(arguments(ar(int_()), vb(x(), LOWER, x()), ar(int_())));
    r.add(arguments(tuple(int_()), vb(x(), LOWER, x()), tuple(int_())));

    // `X`
    r.add(arguments(x(), vb(x(), LOWER, x()), x()));
    r.add(arguments(x(), vb(x(), LOWER, a()), a()));
    r.add(arguments(x(), vb(x(), LOWER, ar(a())), ar(a())));
    r.add(arguments(x(), vb(x(), LOWER, tuple()), tuple()));

    // array[X]
    r.add(arguments(ar(x()), vb(x(), LOWER, a()), ar(a())));
    r.add(arguments(ar(x()), vb(x(), LOWER, ar(a())), ar(ar(a()))));
    r.add(arguments(ar(x()), vb(x(), LOWER, tuple()), ar(tuple())));

    // {X}
    r.add(arguments(tuple(x()), vb(x(), LOWER, a()), tuple(a())));
    r.add(arguments(tuple(x()), vb(x(), LOWER, ar(a())), tuple(ar(a()))));
    r.add(arguments(tuple(x()), vb(x(), LOWER, tuple(a())), tuple(tuple(a()))));

    // X(Y)
    r.add(arguments(f(x(), y()), vb(x(), LOWER, a(), y(), UPPER, b()), f(a(), b())));

    return r;
  }

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

  private static Sides<TypeB> oneSideBound(Side side, TypeB type) {
    return typeF().oneSideBound(side, type);
  }

  private static VarBoundsB vb(
      VarB var1, Side side1, VarB bound1,
      VarB var2, Side side2, VarB bound2) {
    Sides<TypeB> bounds1 = oneSideBound(side1, bound1);
    Sides<TypeB> bounds2 = oneSideBound(side2, bound2);
    if (var1.equals(var2)) {
      return varBoundsB(new BoundedB(var1, typing().merge(bounds1, bounds2)));
    } else {
      return new VarBoundsB(ImmutableMap.of(
          var1, new BoundedB(var1, bounds1),
          var2, new BoundedB(var2, bounds2)
      ));
    }
  }

  private static VarBoundsB vb(VarB var, Side side, TypeB bound) {
    return varBoundsB(new BoundedB(var, oneSideBound(side, bound)));
  }

  private static VarBoundsB vb() {
    return varBoundsB();
  }

  private static VarB a() {
    return testingT().varA();
  }

  private static VarB b() {
    return testingT().varB();
  }

  private static VarB x() {
    return testingT().varX();
  }

  private static VarB y() {
    return testingT().varY();
  }

  private static VarSetB vs(VarB... elements) {
    return varSetB(elements);
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

  private static TypeBF typeF() {
    return typing().typeF();
  }

  private static TypingB typing() {
    return TESTED_ASSIGN_CASES_B.testingT().typing();
  }
}