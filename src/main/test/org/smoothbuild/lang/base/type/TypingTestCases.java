package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypeGraph.buildGraph;
import static org.smoothbuild.lang.base.type.api.VarBounds.varBounds;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.provider.Arguments;
import org.smoothbuild.db.bytecode.type.val.NothingTB;
import org.smoothbuild.lang.base.type.api.Bounded;
import org.smoothbuild.lang.base.type.api.Bounds;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.TypeFactory;
import org.smoothbuild.lang.base.type.api.Var;
import org.smoothbuild.lang.base.type.api.VarBounds;
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
    ArrayList<Arguments> result = new ArrayList<>();

    result.add(arguments(any(), any(), true));
    result.add(arguments(any(), blob(), false));
    result.add(arguments(any(), bool(), false));
    result.add(arguments(any(), int_(), false));
    result.add(arguments(any(), nothing(), false));
    result.add(arguments(any(), string(), false));
    if (isStructSupported()) {
      result.add(arguments(any(), struct(), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(any(), tuple(), false));
    }

    result.add(arguments(any(), a(any()), false));
    result.add(arguments(any(), a(blob()), false));
    result.add(arguments(any(), a(bool()), false));
    result.add(arguments(any(), a(int_()), false));
    result.add(arguments(any(), a(nothing()), false));
    result.add(arguments(any(), a(string()), false));
    if (isStructSupported()) {
      result.add(arguments(any(), a(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(any(), a(tuple()), false));
    }

    result.add(arguments(any(), f(any()), false));
    result.add(arguments(any(), f(blob()), false));
    result.add(arguments(any(), f(bool()), false));
    result.add(arguments(any(), f(int_()), false));
    result.add(arguments(any(), f(nothing()), false));
    result.add(arguments(any(), f(string()), false));
    if (isStructSupported()) {
      result.add(arguments(any(), f(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(any(), f(tuple()), false));
    }

    result.add(arguments(blob(), any(), false));
    result.add(arguments(blob(), blob(), true));
    result.add(arguments(blob(), bool(), false));
    result.add(arguments(blob(), int_(), false));
    result.add(arguments(blob(), nothing(), false));
    result.add(arguments(blob(), string(), false));
    if (isStructSupported()) {
      result.add(arguments(blob(), struct(), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(blob(), tuple(), false));
    }

    result.add(arguments(blob(), a(any()), false));
    result.add(arguments(blob(), a(blob()), false));
    result.add(arguments(blob(), a(bool()), false));
    result.add(arguments(blob(), a(int_()), false));
    result.add(arguments(blob(), a(nothing()), false));
    result.add(arguments(blob(), a(string()), false));
    if (isStructSupported()) {
      result.add(arguments(blob(), a(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(blob(), a(tuple()), false));
    }

    result.add(arguments(blob(), f(any()), false));
    result.add(arguments(blob(), f(blob()), false));
    result.add(arguments(blob(), f(bool()), false));
    result.add(arguments(blob(), f(int_()), false));
    result.add(arguments(blob(), f(nothing()), false));
    result.add(arguments(blob(), f(string()), false));
    if (isStructSupported()) {
      result.add(arguments(blob(), f(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(blob(), f(tuple()), false));
    }

    result.add(arguments(a(blob()), any(), false));
    result.add(arguments(a(blob()), blob(), true));
    result.add(arguments(a(blob()), bool(), false));
    result.add(arguments(a(blob()), int_(), false));
    result.add(arguments(a(blob()), nothing(), false));
    result.add(arguments(a(blob()), string(), false));
    if (isStructSupported()) {
      result.add(arguments(a(blob()), struct(), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(a(blob()), tuple(), false));
    }

    result.add(arguments(a(blob()), a(any()), false));
    result.add(arguments(a(blob()), a(blob()), true));
    result.add(arguments(a(blob()), a(bool()), false));
    result.add(arguments(a(blob()), a(int_()), false));
    result.add(arguments(a(blob()), a(nothing()), false));
    result.add(arguments(a(blob()), a(string()), false));
    if (isStructSupported()) {
      result.add(arguments(a(blob()), a(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(a(blob()), a(tuple()), false));
    }

    result.add(arguments(a(blob()), f(any()), false));
    result.add(arguments(a(blob()), f(blob()), false));
    result.add(arguments(a(blob()), f(bool()), false));
    result.add(arguments(a(blob()), f(int_()), false));
    result.add(arguments(a(blob()), f(nothing()), false));
    result.add(arguments(a(blob()), f(string()), false));
    if (isStructSupported()) {
      result.add(arguments(a(blob()), f(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(a(blob()), f(tuple()), false));
    }

    if (isTupleSupported()) {
      result.add(arguments(tuple(blob()), any(), false));
      result.add(arguments(tuple(blob()), blob(), true));
      result.add(arguments(tuple(blob()), bool(), false));
      result.add(arguments(tuple(blob()), int_(), false));
      result.add(arguments(tuple(blob()), nothing(), false));
      result.add(arguments(tuple(blob()), string(), false));
      if (isStructSupported()) {
        result.add(arguments(tuple(blob()), struct(), false));
      }

      result.add(arguments(tuple(blob()), tuple(any()), false));
      result.add(arguments(tuple(blob()), tuple(blob()), true));
      result.add(arguments(tuple(blob()), tuple(bool()), false));
      result.add(arguments(tuple(blob()), tuple(int_()), false));
      result.add(arguments(tuple(blob()), tuple(nothing()), false));
      result.add(arguments(tuple(blob()), tuple(string()), false));
      if (isStructSupported()) {
        result.add(arguments(tuple(blob()), tuple(struct()), false));
      }

      result.add(arguments(tuple(blob()), a(any()), false));
      result.add(arguments(tuple(blob()), a(blob()), false));
      result.add(arguments(tuple(blob()), a(bool()), false));
      result.add(arguments(tuple(blob()), a(int_()), false));
      result.add(arguments(tuple(blob()), a(nothing()), false));
      result.add(arguments(tuple(blob()), a(string()), false));
      if (isStructSupported()) {
        result.add(arguments(tuple(blob()), a(struct()), false));
      }
      if (isTupleSupported()) {
        result.add(arguments(tuple(blob()), a(tuple()), false));
      }

      result.add(arguments(tuple(blob()), f(any()), false));
      result.add(arguments(tuple(blob()), f(blob()), false));
      result.add(arguments(tuple(blob()), f(bool()), false));
      result.add(arguments(tuple(blob()), f(int_()), false));
      result.add(arguments(tuple(blob()), f(nothing()), false));
      result.add(arguments(tuple(blob()), f(string()), false));
      if (isStructSupported()) {
        result.add(arguments(tuple(blob()), f(struct()), false));
      }
      if (isTupleSupported()) {
        result.add(arguments(tuple(blob()), f(tuple()), false));
      }
    }

    result.add(arguments(f(blob()), any(), false));
    result.add(arguments(f(blob()), blob(), true));
    result.add(arguments(f(blob()), bool(), false));
    result.add(arguments(f(blob()), int_(), false));
    result.add(arguments(f(blob()), nothing(), false));
    result.add(arguments(f(blob()), string(), false));
    if (isStructSupported()) {
      result.add(arguments(f(blob()), struct(), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(blob()), tuple(), false));
    }

    result.add(arguments(f(blob()), a(any()), false));
    result.add(arguments(f(blob()), a(blob()), false));
    result.add(arguments(f(blob()), a(bool()), false));
    result.add(arguments(f(blob()), a(int_()), false));
    result.add(arguments(f(blob()), a(nothing()), false));
    result.add(arguments(f(blob()), a(string()), false));
    if (isStructSupported()) {
      result.add(arguments(f(blob()), a(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(blob()), a(tuple()), false));
    }

    result.add(arguments(f(blob()), f(any()), false));
    result.add(arguments(f(blob()), f(blob()), true));
    result.add(arguments(f(blob()), f(bool()), false));
    result.add(arguments(f(blob()), f(int_()), false));
    result.add(arguments(f(blob()), f(nothing()), false));
    result.add(arguments(f(blob()), f(string()), false));
    if (isStructSupported()) {
      result.add(arguments(f(blob()), f(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(blob()), f(tuple()), false));
    }

    result.add(arguments(f(string(), blob()), any(), false));
    result.add(arguments(f(string(), blob()), blob(), true));
    result.add(arguments(f(string(), blob()), bool(), false));
    result.add(arguments(f(string(), blob()), int_(), false));
    result.add(arguments(f(string(), blob()), nothing(), false));
    result.add(arguments(f(string(), blob()), string(), true));
    if (isStructSupported()) {
      result.add(arguments(f(string(), blob()), struct(), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(string(), blob()), tuple(), false));
    }

    result.add(arguments(f(string(), blob()), a(any()), false));
    result.add(arguments(f(string(), blob()), a(blob()), false));
    result.add(arguments(f(string(), blob()), a(bool()), false));
    result.add(arguments(f(string(), blob()), a(int_()), false));
    result.add(arguments(f(string(), blob()), a(nothing()), false));
    result.add(arguments(f(string(), blob()), a(string()), false));
    if (isStructSupported()) {
      result.add(arguments(f(string(), blob()), a(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(string(), blob()), a(tuple()), false));
    }


    result.add(arguments(f(string(), blob()), f(any()), false));
    result.add(arguments(f(string(), blob()), f(blob()), false));
    result.add(arguments(f(string(), blob()), f(bool()), false));
    result.add(arguments(f(string(), blob()), f(int_()), false));
    result.add(arguments(f(string(), blob()), f(nothing()), false));
    result.add(arguments(f(string(), blob()), f(string()), false));
    if (isStructSupported()) {
      result.add(arguments(f(string(), blob()), f(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(string(), blob()), f(tuple()), false));
    }

    result.add(arguments(f(string(), blob()), f(string(), blob()), true));
    return result;
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

  public List<? extends TestedAssignSpec<TT>> isParamAssignable_test_data() {
    return testedAssignCases.param_assignment_test_specs(true);
  }

  public void inferVarBounds(T type, T assigned, VarBounds<T> expected) {
    assertThat(typing.inferVarBounds(type, assigned, lower()))
        .isEqualTo(expected);
  }

  public List<Arguments> inferVarBounds_test_data() {
    var r = new ArrayList<Arguments>();
    for (T type : concat(testingT().elementaryTypes(), x())) {
      if (type instanceof NothingTS || type instanceof NothingTB) {
        r.add(arguments(a(), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(a(), a(nothing()), vb(a(), lower(), a(nothing()))));
        r.add(arguments(a(), a(a(nothing())), vb(a(), lower(), a(a(nothing())))));

        // arrays
        r.add(arguments(a(a()), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(a(a()), a(nothing()), vb(a(), lower(), nothing())));
        r.add(arguments(a(a()), a(a(nothing())), vb(a(), lower(), a(nothing()))));

        r.add(arguments(a(a(a())), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(a(a(a())), a(nothing()), vb(a(), lower(), nothing())));
        r.add(arguments(a(a(a())), a(a(nothing())), vb(a(), lower(), nothing())));

        // tuples
        if (isTupleSupported()) {
          r.add(arguments(tuple(a()), nothing(), vb(a(), lower(), nothing())));
          r.add(arguments(tuple(a()), tuple(nothing()), vb(a(), lower(), nothing())));
          r.add(arguments(tuple(a()), tuple(tuple(nothing())), vb(a(), lower(), tuple(nothing()))));

          r.add(arguments(tuple(tuple(a())), nothing(), vb(a(), lower(), nothing())));
          r.add(arguments(tuple(tuple(a())), tuple(nothing()), vb(a(), lower(), nothing())));
          r.add(arguments(tuple(tuple(a())), tuple(tuple(nothing())), vb(a(), lower(), nothing())));
        }

        // funcs
        r.add(arguments(f(a()), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(f(f(a())), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(f(f(f(a()))), nothing(), vb(a(), lower(), nothing())));

        r.add(arguments(f(bool(), a()), nothing(), vb(a(), upper(), any())));
        r.add(arguments(f(bool(), f(a())), nothing(), vb(a(), upper(), any())));
        r.add(arguments(f(bool(), f(f(a()))), nothing(), vb(a(), upper(), any())));

        r.add(arguments(f(bool(), f(blob(), a())), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(f(bool(), f(blob(), f(a()))), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(f(bool(), f(blob(), f(f(a())))), nothing(), vb(a(), lower(), nothing())));

        // arrays + funcs
        r.add(arguments(a(f(a())), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(a(f(string(), a())), nothing(), vb(a(), upper(), any())));

        r.add(arguments(f(a(a())), nothing(), vb(a(), lower(), nothing())));
        r.add(arguments(f(bool(), a(a())), nothing(), vb(a(), upper(), any())));
      } else {
        r.add(arguments(a(), type, vb(a(), lower(), type)));
        r.add(arguments(a(), a(type), vb(a(), lower(), a(type))));
        r.add(arguments(a(), a(a(type)), vb(a(), lower(), a(a(type)))));

        // arrays
        r.add(arguments(a(a()), type, vb()));
        r.add(arguments(a(a()), a(type), vb(a(), lower(), type)));
        r.add(arguments(a(a()), a(a(type)), vb(a(), lower(), a(type))));

        r.add(arguments(a(a(a())), type, vb()));
        r.add(arguments(a(a(a())), a(type), vb()));
        r.add(arguments(a(a(a())), a(a(type)), vb(a(), lower(), type)));

        // tuples
        if (isTupleSupported()) {
          r.add(arguments(tuple(a()), type, vb()));
          r.add(arguments(tuple(a()), tuple(type), vb(a(), lower(), type)));
          r.add(arguments(tuple(a()), tuple(tuple(type)), vb(a(), lower(), tuple(type))));

          r.add(arguments(tuple(tuple(a())), type, vb()));
          r.add(arguments(tuple(tuple(a())), tuple(type), vb()));
          r.add(arguments(tuple(tuple(a())), tuple(tuple(type)), vb(a(), lower(), type)));
        }

        // funcs
        r.add(arguments(f(a()), type, vb()));
        r.add(arguments(f(a()), f(type), vb(a(), lower(), type)));
        r.add(arguments(f(a()), f(f(type)), vb(a(), lower(), f(type))));
        r.add(arguments(f(a()), f(f(f(type))), vb(a(), lower(), f(f(type)))));

        r.add(arguments(f(f(a())), type, vb()));
        r.add(arguments(f(f(a())), f(type), vb()));
        r.add(arguments(f(f(a())), f(f(type)), vb(a(), lower(), type)));
        r.add(arguments(f(f(a())), f(f(f(type))), vb(a(), lower(), f(type))));

        r.add(arguments(f(f(f(a()))), type, vb()));
        r.add(arguments(f(f(f(a()))), f(type), vb()));
        r.add(arguments(f(f(f(a()))), f(f(type)), vb()));
        r.add(arguments(f(f(f(a()))), f(f(f(type))), vb(a(), lower(), type)));

        r.add(arguments(f(bool(), a()), f(bool(), type), vb(a(), upper(), type)));
        r.add(arguments(f(bool(), f(a())), f(bool(), f(type)), vb(a(), upper(), type)));
        r.add(arguments(f(bool(), f(f(a()))), f(bool(), f(f(type))), vb(a(), upper(), type)));

        r.add(arguments(f(bool(), f(blob(), a())), f(bool(), f(blob(), type)), vb(a(), lower(), type)));
        r.add(arguments(f(bool(), f(blob(), f(a()))), f(bool(), f(blob(), f(type))), vb(a(), lower(), type)));
        r.add(arguments(f(bool(), f(blob(), f(f(a())))), f(bool(), f(blob(), f(f(type)))), vb(a(), lower(), type)));

        // arrays + funcs
        r.add(arguments(a(f(a())), a(f(type)), vb(a(), lower(), type)));
        r.add(arguments(a(f(bool(), a())), a(f(bool(), type)), vb(a(), upper(), type)));

        r.add(arguments(f(a(a())), f(a(type)), vb(a(), lower(), type)));
        r.add(arguments(f(bool(), a(a())), f(bool(), a(type)), vb(a(), upper(), type)));
      }
    }
    return r;
  }

  public void mapVars(T type, VarBounds<T> varBounds, Type expected) {
    assertThat(typing.mapVars(type, varBounds, lower()))
        .isEqualTo(expected);
  }

  public List<Arguments> mapVars_test_data() {
    var result = new ArrayList<Arguments>();
    result.add(arguments(x(), vb(), x()));
    result.add(arguments(a(x()), vb(), a(x())));
    for (T type : testingT().allTestedTypes()) {
      result.add(arguments(x(), vb(x(), lower(), type), type));
      result.add(arguments(x(), vb(x(), lower(), a(type)), a(type)));
      if (isTupleSupported()) {
        result.add(arguments(x(), vb(x(), lower(), tuple(type)), tuple(type)));
      }
      result.add(arguments(a(x()), vb(x(), lower(), type), a(type)));
      result.add(arguments(a(x()), vb(x(), lower(), a(type)), a(a(type))));
      if (isTupleSupported()) {
        result.add(arguments(a(x()), vb(x(), lower(), tuple(type)), a(tuple(type))));
        result.add(arguments(tuple(x()), vb(x(), lower(), type), tuple(type)));
        result.add(arguments(tuple(x()), vb(x(), lower(), a(type)), tuple(a(type))));
      }
    }
    for (T newA : testingT().allTestedTypes()) {
      for (T newB : testingT().allTestedTypes()) {
        result.add(arguments(f(a(), b()), vb(a(), lower(), newA, b(), upper(), newB), f(newA, newB)));
      }
    }
    for (T type : testingT().elementaryTypes()) {
      result.add(arguments(type, vb(), type));

      result.add(arguments(a(type), vb(), a(type)));
      result.add(arguments(a(a(type)), vb(), a(a(type))));

      if (isTupleSupported()) {
        result.add(arguments(tuple(type), vb(), tuple(type)));
        result.add(arguments(tuple(tuple(type)), vb(), tuple(tuple(type))));
      }

      result.add(arguments(f(type), vb(), f(type)));
      result.add(arguments(f(bool(), type), vb(), f(bool(), type)));
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
    return buildGraph(testingT().typesForBuildWideGraph(), 1, testingT());
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

  public VarBounds<T> vb(
      T var1, Side<T> side1, T bound1,
      T var2, Side<T> side2, T bound2) {
    Bounds<T> bounds1 = oneSideBound(side1, bound1);
    Bounds<T> bounds2 = oneSideBound(side2, bound2);
    if (var1.equals(var2)) {
      return varBounds(new Bounded<>((Var) var1, typing.merge(bounds1, bounds2)));
    } else {
      return new VarBounds<>(ImmutableMap.of(
          (Var) var1, new Bounded<>((Var) var1, bounds1),
          (Var) var2, new Bounded<>((Var) var2, bounds2)
      ));
    }
  }

  public VarBounds<T> vb(T var, Side<T> side, T bound) {
    return varBounds(new Bounded<>((Var) var, oneSideBound(side, bound)));
  }

  public VarBounds<T> vb() {
    return varBounds();
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

  private boolean isStructSupported() {
    return testingT().isStructSupported();
  }

  private T struct() {
    return testingT().struct();
  }

  private boolean isTupleSupported() {
    return testingT().isTupleSupported();
  }

  private T tuple() {
    return testingT().tuple();
  }

  private T tuple(T item) {
    return testingT().tuple(list(item));
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
