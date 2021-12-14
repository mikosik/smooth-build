package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypeGraph.buildGraph;
import static org.smoothbuild.lang.base.type.api.BoundsMap.boundsMap;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.provider.Arguments;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Var;
import org.smoothbuild.lang.base.type.impl.NothingTS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class TypingTestCases<T extends Type, TT extends TestedT<T>> {
  private final Typing<T> typing;
  private final TestedAssignCases<T, TT, ? extends TestedAssignSpec<TT>> testedAssignCases;

  public TypingTestCases(
      TestedAssignCases<T, TT, ? extends TestedAssignSpec<TT>> testedAssignCases) {
    this.typing = testedAssignCases.testingT().typing();
    this.testedAssignCases = testedAssignCases;
  }

  public void testContains(T type, T contained, boolean expected) {
    assertThat(typing.contains(type, contained))
        .isEqualTo(expected);
  }

  public List<Arguments> contains_test_data() {
    return list(
        arguments(any(), any(), true),
        arguments(any(), blob(), false),
        arguments(any(), bool(), false),
        arguments(any(), int_(), false),
        arguments(any(), nothing(), false),
        arguments(any(), string(), false),
        arguments(any(), struct(), false),

        arguments(any(), a(any()), false),
        arguments(any(), a(blob()), false),
        arguments(any(), a(bool()), false),
        arguments(any(), a(int_()), false),
        arguments(any(), a(nothing()), false),
        arguments(any(), a(string()), false),
        arguments(any(), a(struct()), false),

        arguments(any(), f(any()), false),
        arguments(any(), f(blob()), false),
        arguments(any(), f(bool()), false),
        arguments(any(), f(int_()), false),
        arguments(any(), f(nothing()), false),
        arguments(any(), f(string()), false),
        arguments(any(), f(struct()), false),

        arguments(blob(), any(), false),
        arguments(blob(), blob(), true),
        arguments(blob(), bool(), false),
        arguments(blob(), int_(), false),
        arguments(blob(), nothing(), false),
        arguments(blob(), string(), false),
        arguments(blob(), struct(), false),

        arguments(blob(), a(any()), false),
        arguments(blob(), a(blob()), false),
        arguments(blob(), a(bool()), false),
        arguments(blob(), a(int_()), false),
        arguments(blob(), a(nothing()), false),
        arguments(blob(), a(string()), false),
        arguments(blob(), a(struct()), false),

        arguments(blob(), f(any()), false),
        arguments(blob(), f(blob()), false),
        arguments(blob(), f(bool()), false),
        arguments(blob(), f(int_()), false),
        arguments(blob(), f(nothing()), false),
        arguments(blob(), f(string()), false),
        arguments(blob(), f(struct()), false),

        arguments(a(blob()), any(), false),
        arguments(a(blob()), blob(), true),
        arguments(a(blob()), bool(), false),
        arguments(a(blob()), int_(), false),
        arguments(a(blob()), nothing(), false),
        arguments(a(blob()), string(), false),
        arguments(a(blob()), struct(), false),

        arguments(a(blob()), a(any()), false),
        arguments(a(blob()), a(blob()), true),
        arguments(a(blob()), a(bool()), false),
        arguments(a(blob()), a(int_()), false),
        arguments(a(blob()), a(nothing()), false),
        arguments(a(blob()), a(string()), false),
        arguments(a(blob()), a(struct()), false),

        arguments(a(blob()), f(any()), false),
        arguments(a(blob()), f(blob()), false),
        arguments(a(blob()), f(bool()), false),
        arguments(a(blob()), f(int_()), false),
        arguments(a(blob()), f(nothing()), false),
        arguments(a(blob()), f(string()), false),
        arguments(a(blob()), f(struct()), false),

        arguments(f(blob()), any(), false),
        arguments(f(blob()), blob(), true),
        arguments(f(blob()), bool(), false),
        arguments(f(blob()), int_(), false),
        arguments(f(blob()), nothing(), false),
        arguments(f(blob()), string(), false),
        arguments(f(blob()), struct(), false),

        arguments(f(blob()), a(any()), false),
        arguments(f(blob()), a(blob()), false),
        arguments(f(blob()), a(bool()), false),
        arguments(f(blob()), a(int_()), false),
        arguments(f(blob()), a(nothing()), false),
        arguments(f(blob()), a(string()), false),
        arguments(f(blob()), a(struct()), false),

        arguments(f(blob()), f(any()), false),
        arguments(f(blob()), f(blob()), true),
        arguments(f(blob()), f(bool()), false),
        arguments(f(blob()), f(int_()), false),
        arguments(f(blob()), f(nothing()), false),
        arguments(f(blob()), f(string()), false),
        arguments(f(blob()), f(struct()), false),

        arguments(f(string(), blob()), any(), false),
        arguments(f(string(), blob()), blob(), true),
        arguments(f(string(), blob()), bool(), false),
        arguments(f(string(), blob()), int_(), false),
        arguments(f(string(), blob()), nothing(), false),
        arguments(f(string(), blob()), string(), true),
        arguments(f(string(), blob()), struct(), false),

        arguments(f(string(), blob()), a(any()), false),
        arguments(f(string(), blob()), a(blob()), false),
        arguments(f(string(), blob()), a(bool()), false),
        arguments(f(string(), blob()), a(int_()), false),
        arguments(f(string(), blob()), a(nothing()), false),
        arguments(f(string(), blob()), a(string()), false),
        arguments(f(string(), blob()), a(struct()), false),

        arguments(f(string(), blob()), f(any()), false),
        arguments(f(string(), blob()), f(blob()), false),
        arguments(f(string(), blob()), f(bool()), false),
        arguments(f(string(), blob()), f(int_()), false),
        arguments(f(string(), blob()), f(nothing()), false),
        arguments(f(string(), blob()), f(string()), false),
        arguments(f(string(), blob()), f(struct()), false),

        arguments(f(string(), blob()), f(string(), blob()), true)
    );
  }

  public void isAssignable(TestedAssignSpec<? extends TestedT<T>> spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(typing.isAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  public List<? extends TestedAssignSpec<TT>> isAssignable_test_data() {
    return testedAssignCases.assignment_test_specs(true);
  }

  public void isParamAssignable(TestedAssignSpec<? extends TestedT<T>> spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(typing.isParamAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  public List<TestedAssignSpecS> isParamAssignable_test_data() {
    return TestedAssignCases.INSTANCE_S.param_assignment_test_specs(true);
  }

  public void inferVarBounds(T type, T assigned, BoundsMap<T> expected) {
    assertThat(typing.inferVarBounds(type, assigned, lower()))
        .isEqualTo(expected);
  }

  public List<Arguments> inferVarBounds_test_data() {
    var r = new ArrayList<Arguments>();
    for (T type : concat(testingT().elementaryTypes(), x())) {
      if (type instanceof NothingTS) {
        // arrays
        r.add(arguments(a(), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(a(), a(nothing()), bm(a(), lower(), a(nothing()))));
        r.add(arguments(a(), a(a(nothing())), bm(a(), lower(), a(a(nothing())))));

        r.add(arguments(a(a()), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(a(a()), a(nothing()), bm(a(), lower(), nothing())));
        r.add(arguments(a(a()), a(a(nothing())), bm(a(), lower(), a(nothing()))));

        r.add(arguments(a(a(a())), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(a(a(a())), a(nothing()), bm(a(), lower(), nothing())));
        r.add(arguments(a(a(a())), a(a(nothing())), bm(a(), lower(), nothing())));

        // funcs
        r.add(arguments(f(a()), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(f(f(a())), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(f(f(f(a()))), nothing(), bm(a(), lower(), nothing())));

        r.add(arguments(f(bool(), a()), nothing(), bm(a(), upper(), any())));
        r.add(arguments(f(bool(), f(a())), nothing(), bm(a(), upper(), any())));
        r.add(arguments(f(bool(), f(f(a()))), nothing(), bm(a(), upper(), any())));

        r.add(arguments(f(bool(), f(blob(), a())), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(f(bool(), f(blob(), f(a()))), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(f(bool(), f(blob(), f(f(a())))), nothing(), bm(a(), lower(), nothing())));

        // arrays + funcs
        r.add(arguments(a(f(a())), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(a(f(string(), a())), nothing(), bm(a(), upper(), any())));

        r.add(arguments(f(a(a())), nothing(), bm(a(), lower(), nothing())));
        r.add(arguments(f(bool(), a(a())), nothing(), bm(a(), upper(), any())));
      } else {
        // arrays
        r.add(arguments(a(), type, bm(a(), lower(), type)));
        r.add(arguments(a(), a(type), bm(a(), lower(), a(type))));
        r.add(arguments(a(), a(a(type)), bm(a(), lower(), a(a(type)))));

        r.add(arguments(a(a()), type, bm()));
        r.add(arguments(a(a()), a(type), bm(a(), lower(), type)));
        r.add(arguments(a(a()), a(a(type)), bm(a(), lower(), a(type))));

        r.add(arguments(a(a(a())), type, bm()));
        r.add(arguments(a(a(a())), a(type), bm()));
        r.add(arguments(a(a(a())), a(a(type)), bm(a(), lower(), type)));

        // funcs
        r.add(arguments(f(a()), type, bm()));
        r.add(arguments(f(a()), f(type), bm(a(), lower(), type)));
        r.add(arguments(f(a()), f(f(type)), bm(a(), lower(), f(type))));
        r.add(arguments(f(a()), f(f(f(type))), bm(a(), lower(), f(f(type)))));

        r.add(arguments(f(f(a())), type, bm()));
        r.add(arguments(f(f(a())), f(type), bm()));
        r.add(arguments(f(f(a())), f(f(type)), bm(a(), lower(), type)));
        r.add(arguments(f(f(a())), f(f(f(type))), bm(a(), lower(), f(type))));

        r.add(arguments(f(f(f(a()))), type, bm()));
        r.add(arguments(f(f(f(a()))), f(type), bm()));
        r.add(arguments(f(f(f(a()))), f(f(type)), bm()));
        r.add(arguments(f(f(f(a()))), f(f(f(type))), bm(a(), lower(), type)));

        r.add(arguments(f(bool(), a()), f(bool(), type), bm(a(), upper(), type)));
        r.add(arguments(f(bool(), f(a())), f(bool(), f(type)), bm(a(), upper(), type)));
        r.add(arguments(f(bool(), f(f(a()))), f(bool(), f(f(type))), bm(a(), upper(), type)));

        r.add(arguments(f(bool(), f(blob(), a())), f(bool(), f(blob(), type)), bm(a(), lower(), type)));
        r.add(arguments(f(bool(), f(blob(), f(a()))), f(bool(), f(blob(), f(type))), bm(a(), lower(), type)));
        r.add(arguments(f(bool(), f(blob(), f(f(a())))), f(bool(), f(blob(), f(f(type)))), bm(a(), lower(), type)));

        // arrays + funcs
        r.add(arguments(a(f(a())), a(f(type)), bm(a(), lower(), type)));
        r.add(arguments(a(f(bool(), a())), a(f(bool(), type)), bm(a(), upper(), type)));

        r.add(arguments(f(a(a())), f(a(type)), bm(a(), lower(), type)));
        r.add(arguments(f(bool(), a(a())), f(bool(), a(type)), bm(a(), upper(), type)));
      }
    }
    return r;
  }

  public void mapVars(T type, BoundsMap<T> boundsMap, Type expected) {
    assertThat(typing.mapVars(type, boundsMap, lower()))
        .isEqualTo(expected);
  }

  public List<Arguments> mapVars_test_data() {
    var result = new ArrayList<Arguments>();
    for (T type : testingT().allTestedTypes()) {
      result.add(arguments(x(), bm(), x()));
      result.add(arguments(a(x()), bm(), a(x())));
      result.add(arguments(x(), bm(x(), lower(), type), type));
      result.add(arguments(a(x()), bm(x(), lower(), type), a(type)));
      result.add(arguments(x(), bm(x(), lower(), a(type)), a(type)));
      result.add(arguments(a(x()), bm(x(), lower(), a(type)), a(a(type))));
    }
    for (T newA : testingT().allTestedTypes()) {
      for (T newB : testingT().allTestedTypes()) {
        result.add(arguments(f(a(), b()), bm(a(), lower(), newA, b(), upper(), newB), f(newA, newB)));
      }
    }
    for (T type : testingT().elementaryTypes()) {
      result.add(arguments(type, bm(), type));
      result.add(arguments(a(a(type)), bm(), a(a(type))));

      result.add(arguments(f(type), bm(), f(type)));
      result.add(arguments(f(bool(), type), bm(), f(bool(), type)));
    }
    return result;
  }

  public void merge_up_wide_graph(T type1, T type2, T expected) {
    testMergeBothWays(type1, type2, expected, upper());
  }

  public Collection<Arguments> merge_up_wide_graph_cases() {
    return buildWideGraph()
        .buildTestCases(nothing());
  }

  public void merge_up_deep_graph(T type1, T type2, T expected) {
    testMergeBothWays(type1, type2, expected, upper());
  }

  public Collection<Arguments> merge_up_deep_graph_cases() {
    return buildGraph(list(blob()), 2, testingT())
        .buildTestCases(nothing());
  }

  public void merge_down_wide_graph(T type1, T type2, T expected) {
    testMergeBothWays(type1, type2, expected, lower());
  }

  public Collection<Arguments> merge_down_wide_graph_cases() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(any());
  }

  public void merge_down_deep_graph(T type1, T type2, Type expected) {
    testMergeBothWays(type1, type2, expected, lower());
  }

  public Collection<Arguments> merge_down_deep_graph_cases() {
    return buildGraph(list(blob()), 2, testingT())
        .inverse()
        .buildTestCases(any());
  }

  private void testMergeBothWays(T type1, T type2, Type expected, Side<T> direction) {
    assertThat(typing.merge(type1, type2, direction))
        .isEqualTo(expected);
    assertThat(typing.merge(type2, type1, direction))
        .isEqualTo(expected);
  }

  private TestingTypeGraph<T> buildWideGraph() {
    if (testingT().baseTypes().size() != 5) {
      throw new RuntimeException("Add missing type to list below.");
    }
    return buildGraph(list(a(), b(), blob(), bool(), int_(), struct(), string()), 1, testingT());
  }

  private Side<T> lower() {
    return factory().lower();
  }

  private Side<T> upper() {
    return factory().upper();
  }

  public Bounds<T> oneSideBound(Side<T> side, T type) {
    return factory().oneSideBound(side, type);
  }

  public BoundsMap<T> bm(
      T var1, Side<T> side1, T bound1,
      T var2, Side<T> side2, T bound2) {
    Bounds<T> bounds1 = oneSideBound(side1, bound1);
    Bounds<T> bounds2 = oneSideBound(side2, bound2);
    if (var1.equals(var2)) {
      return boundsMap(new Bounded<>((Var) var1, typing.merge(bounds1, bounds2)));
    } else {
      return new BoundsMap<>(ImmutableMap.of(
          (Var) var1, new Bounded<>((Var) var1, bounds1),
          (Var) var2, new Bounded<>((Var) var2, bounds2)
      ));
    }
  }

  public BoundsMap<T> bm(T var, Side<T> side, T bound) {
    return boundsMap(new Bounded<>((Var) var, oneSideBound(side, bound)));
  }

  public BoundsMap<T> bm() {
    return boundsMap();
  }

  private T a() {
    return testingT().a();
  }

  private T b() {
    return testingT().b();
  }

  private T x() {
    return testingT().x();
  }

  private T any() {
    return testingT().any();
  }

  private T blob() {
    return testingT().blob();
  }

  private T bool() {
    return testingT().bool();
  }

  private T int_() {
    return testingT().int_();
  }

  private T nothing() {
    return testingT().nothing();
  }

  private T string() {
    return testingT().string();
  }

  private T struct() {
    return testingT().struct();
  }

  public T a(T elemT) {
    return testingT().array(elemT);
  }

  public T f(T resT, T... paramTs) {
    return f(resT, list(paramTs));
  }

  public T f(T resT) {
    return f(resT, list());
  }

  public T f(T resT, ImmutableList<T> paramTs) {
    return testingT().func(resT, paramTs);
  }

  private TestingT<T> testingT() {
    return testedFactory().testingT();
  }

  private TestedTFactory<T, TT, ? extends TestedAssignSpec<TT>> testedFactory() {
    return testedAssignCases.testedTFactory();
  }

  private TypeFactory<T> factory() {
    return typing.factory();
  }
}
