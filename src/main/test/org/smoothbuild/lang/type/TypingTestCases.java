package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.type.TestingTypeGraph.buildGraph;
import static org.smoothbuild.lang.type.api.Side.LOWER;
import static org.smoothbuild.lang.type.api.Side.UPPER;
import static org.smoothbuild.lang.type.api.VarBounds.varBounds;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.provider.Arguments;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Type;
import org.smoothbuild.lang.type.api.TypeF;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBounds;
import org.smoothbuild.lang.type.api.VarSet;
import org.smoothbuild.lang.type.impl.NothingTS;
import org.smoothbuild.testing.type.TestedAssignCases;
import org.smoothbuild.testing.type.TestedAssignSpec;
import org.smoothbuild.testing.type.TestedT;
import org.smoothbuild.testing.type.TestedTF;
import org.smoothbuild.testing.type.TestingT;

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

    result.add(arguments(any(), ar(any()), false));
    result.add(arguments(any(), ar(blob()), false));
    result.add(arguments(any(), ar(bool()), false));
    result.add(arguments(any(), ar(int_()), false));
    result.add(arguments(any(), ar(nothing()), false));
    result.add(arguments(any(), ar(string()), false));
    if (isStructSupported()) {
      result.add(arguments(any(), ar(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(any(), ar(tuple()), false));
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

    result.add(arguments(blob(), ar(any()), false));
    result.add(arguments(blob(), ar(blob()), false));
    result.add(arguments(blob(), ar(bool()), false));
    result.add(arguments(blob(), ar(int_()), false));
    result.add(arguments(blob(), ar(nothing()), false));
    result.add(arguments(blob(), ar(string()), false));
    if (isStructSupported()) {
      result.add(arguments(blob(), ar(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(blob(), ar(tuple()), false));
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

    result.add(arguments(ar(blob()), any(), false));
    result.add(arguments(ar(blob()), blob(), true));
    result.add(arguments(ar(blob()), bool(), false));
    result.add(arguments(ar(blob()), int_(), false));
    result.add(arguments(ar(blob()), nothing(), false));
    result.add(arguments(ar(blob()), string(), false));
    if (isStructSupported()) {
      result.add(arguments(ar(blob()), struct(), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(ar(blob()), tuple(), false));
    }

    result.add(arguments(ar(blob()), ar(any()), false));
    result.add(arguments(ar(blob()), ar(blob()), true));
    result.add(arguments(ar(blob()), ar(bool()), false));
    result.add(arguments(ar(blob()), ar(int_()), false));
    result.add(arguments(ar(blob()), ar(nothing()), false));
    result.add(arguments(ar(blob()), ar(string()), false));
    if (isStructSupported()) {
      result.add(arguments(ar(blob()), ar(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(ar(blob()), ar(tuple()), false));
    }

    result.add(arguments(ar(blob()), f(any()), false));
    result.add(arguments(ar(blob()), f(blob()), false));
    result.add(arguments(ar(blob()), f(bool()), false));
    result.add(arguments(ar(blob()), f(int_()), false));
    result.add(arguments(ar(blob()), f(nothing()), false));
    result.add(arguments(ar(blob()), f(string()), false));
    if (isStructSupported()) {
      result.add(arguments(ar(blob()), f(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(ar(blob()), f(tuple()), false));
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

      result.add(arguments(tuple(blob()), ar(any()), false));
      result.add(arguments(tuple(blob()), ar(blob()), false));
      result.add(arguments(tuple(blob()), ar(bool()), false));
      result.add(arguments(tuple(blob()), ar(int_()), false));
      result.add(arguments(tuple(blob()), ar(nothing()), false));
      result.add(arguments(tuple(blob()), ar(string()), false));
      if (isStructSupported()) {
        result.add(arguments(tuple(blob()), ar(struct()), false));
      }
      if (isTupleSupported()) {
        result.add(arguments(tuple(blob()), ar(tuple()), false));
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

    result.add(arguments(f(blob()), ar(any()), false));
    result.add(arguments(f(blob()), ar(blob()), false));
    result.add(arguments(f(blob()), ar(bool()), false));
    result.add(arguments(f(blob()), ar(int_()), false));
    result.add(arguments(f(blob()), ar(nothing()), false));
    result.add(arguments(f(blob()), ar(string()), false));
    if (isStructSupported()) {
      result.add(arguments(f(blob()), ar(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(blob()), ar(tuple()), false));
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

    result.add(arguments(f(string(), blob()), ar(any()), false));
    result.add(arguments(f(string(), blob()), ar(blob()), false));
    result.add(arguments(f(string(), blob()), ar(bool()), false));
    result.add(arguments(f(string(), blob()), ar(int_()), false));
    result.add(arguments(f(string(), blob()), ar(nothing()), false));
    result.add(arguments(f(string(), blob()), ar(string()), false));
    if (isStructSupported()) {
      result.add(arguments(f(string(), blob()), ar(struct()), false));
    }
    if (isTupleSupported()) {
      result.add(arguments(f(string(), blob()), ar(tuple()), false));
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
    assertThat(typing.inferVarBounds(type, assigned, LOWER))
        .isEqualTo(expected);
  }

  public List<Arguments> inferVarBounds_test_data() {
    var r = new ArrayList<Arguments>();
    for (T type : concat(testingT().elementaryTypes(), x())) {
      if (type instanceof NothingTS || type instanceof NothingTB) {
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
        if (isTupleSupported()) {
          r.add(arguments(tuple(a()), nothing(), vb(a(), LOWER, nothing())));
          r.add(arguments(tuple(a()), tuple(nothing()), vb(a(), LOWER, nothing())));
          r.add(arguments(tuple(a()), tuple(tuple(nothing())), vb(a(), LOWER, tuple(nothing()))));

          r.add(arguments(tuple(tuple(a())), nothing(), vb(a(), LOWER, nothing())));
          r.add(arguments(tuple(tuple(a())), tuple(nothing()), vb(a(), LOWER, nothing())));
          r.add(arguments(tuple(tuple(a())), tuple(tuple(nothing())), vb(a(), LOWER, nothing())));
        }

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
        if (isTupleSupported()) {
          r.add(arguments(tuple(a()), type, vb()));
          r.add(arguments(tuple(a()), tuple(type), vb(a(), LOWER, type)));
          r.add(arguments(tuple(a()), tuple(tuple(type)), vb(a(), LOWER, tuple(type))));

          r.add(arguments(tuple(tuple(a())), type, vb()));
          r.add(arguments(tuple(tuple(a())), tuple(type), vb()));
          r.add(arguments(tuple(tuple(a())), tuple(tuple(type)), vb(a(), LOWER, type)));
        }

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

  public void mapVarsLower(T type, VarBounds<T> varBounds, Type expected) {
    assertThat(typing.mapVarsLower(type, varBounds))
        .isEqualTo(expected);
  }

  public List<Arguments> mapVarsLower_test_data() {
    var r = new ArrayList<Arguments>();
    // types without variables
    r.add(arguments(int_(), vb(x(), LOWER, x()), int_()));
    r.add(arguments(ar(int_()), vb(x(), LOWER, x()), ar(int_())));
    if (isTupleSupported()) {
      r.add(arguments(tuple(int_()), vb(x(), LOWER, x()), tuple(int_())));
    }
    if (isStructSupported()) {
      r.add(arguments(struct(), vb(x(), LOWER, x()), struct()));
    }

    // `X`
    r.add(arguments(x(), vb(x(), LOWER, x()), x()));
    r.add(arguments(x(), vb(x(), LOWER, a()), a()));
    r.add(arguments(x(), vb(x(), LOWER, ar(a())), ar(a())));
    if (isTupleSupported()) {
      r.add(arguments(x(), vb(x(), LOWER, tuple()), tuple()));
    }
    if (isStructSupported()) {
      r.add(arguments(x(), vb(x(), LOWER, struct()), struct()));
    }

    // array[X]
    r.add(arguments(ar(x()), vb(x(), LOWER, a()), ar(a())));
    r.add(arguments(ar(x()), vb(x(), LOWER, ar(a())), ar(ar(a()))));
    if (isTupleSupported()) {
      r.add(arguments(ar(x()), vb(x(), LOWER, tuple()), ar(tuple())));
    }
    if (isStructSupported()) {
      r.add(arguments(ar(x()), vb(x(), LOWER, struct()), ar(struct())));
    }

    // {X}
    if (isTupleSupported()) {
      r.add(arguments(tuple(x()), vb(x(), LOWER, a()), tuple(a())));
      r.add(arguments(tuple(x()), vb(x(), LOWER, ar(a())), tuple(ar(a()))));
      r.add(arguments(tuple(x()), vb(x(), LOWER, tuple(a())), tuple(tuple(a()))));
    }

    // X(Y)
    r.add(arguments(f(x(), y()), vb(x(), LOWER, a(), y(), UPPER, b()), f(vs(), a(), b())));

    return r;
  }

  public void merge_up_wide_graph(T type1, T type2, T expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  public Collection<Arguments> merge_up_wide_graph_cases() {
    return buildWideGraph()
        .buildTestCases(nothing());
  }

  public void merge_up_deep_graph(T type1, T type2, T expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  public Collection<Arguments> merge_up_deep_graph_cases() {
    return buildGraph(list(blob()), 2, testingT())
        .buildTestCases(nothing());
  }

  public void merge_down_wide_graph(T type1, T type2, T expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  public Collection<Arguments> merge_down_wide_graph_cases() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(any());
  }

  public void merge_down_deep_graph(T type1, T type2, Type expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  public Collection<Arguments> merge_down_deep_graph_cases() {
    return buildGraph(list(blob()), 2, testingT())
        .inverse()
        .buildTestCases(any());
  }

  private void testMergeBothWays(T type1, T type2, Type expected, Side direction) {
    assertThat(typing.merge(type1, type2, direction))
        .isEqualTo(expected);
    assertThat(typing.merge(type2, type1, direction))
        .isEqualTo(expected);
  }

  private TestingTypeGraph<T> buildWideGraph() {
    return buildGraph(testingT().typesForBuildWideGraph(), 1, testingT());
  }

  public Sides<T> oneSideBound(Side side, T type) {
    return typeF().oneSideBound(side, type);
  }

  public VarBounds<T> vb(
      T var1, Side side1, T bound1,
      T var2, Side side2, T bound2) {
    Sides<T> bounds1 = oneSideBound(side1, bound1);
    Sides<T> bounds2 = oneSideBound(side2, bound2);
    if (var1.equals(var2)) {
      return varBounds(new Bounded<>((Var) var1, typing.merge(bounds1, bounds2)));
    } else {
      return new VarBounds<>(ImmutableMap.of(
          (Var) var1, new Bounded<>((Var) var1, bounds1),
          (Var) var2, new Bounded<>((Var) var2, bounds2)
      ));
    }
  }

  public VarBounds<T> vb(T var, Side side, T bound) {
    return varBounds(new Bounded<>((Var) var, oneSideBound(side, bound)));
  }

  public VarBounds<T> vb() {
    return varBounds();
  }

  private T a() {
    return testingT().varA();
  }

  private T b() {
    return testingT().varB();
  }

  private T x() {
    return testingT().varX();
  }

  private T y() {
    return testingT().varY();
  }

  private VarSet<T> vs(Var... elements) {
    return testingT().vs(elements);
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

  public T ar(T elemT) {
    return testingT().array(elemT);
  }

  public T f(T resT, T... paramTs) {
    return f(resT, list(paramTs));
  }

  public T f(VarSet<T> tParams, T resT, T... paramTs) {
    return testingT().func(tParams, resT, list(paramTs));
  }

  public T f(T resT) {
    return f(resT, list());
  }

  public T f(T resT, ImmutableList<T> paramTs) {
    return testingT().func(resT, paramTs);
  }

  private TestingT<T> testingT() {
    return testedF().testingT();
  }

  private TestedTF<T, TT, ? extends TestedAssignSpec<TT>> testedF() {
    return testedAssignCases.testedTF();
  }

  private TypeF<T> typeF() {
    return typing.typeF();
  }
}
