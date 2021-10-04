package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypeGraph.buildGraph;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ALL_TESTED_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.DATA;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.FLAG;
import static org.smoothbuild.lang.base.type.TestingTypes.INT;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.X;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;
import static org.smoothbuild.lang.base.type.TestingTypes.item;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.lang.base.type.constraint.TestingBoundsMap.bm;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.Sides.Side;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;

public class TypingTest extends TestingContext {
  @ParameterizedTest
  @MethodSource("strip_test_data")
  public void strip(Type type, Type expected) {
    assertThat(typing().strip(type))
        .isEqualTo(expected);
  }

  public static List<Arguments> strip_test_data() {
    ImmutableList<Type> unchangedByStripping = ImmutableList.<Type>builder()
        .addAll(BASE_TYPES)
        .add(PERSON)
        .add(f(BLOB))
        .add(f(BLOB, BLOB))
        .add(f(f(BLOB), BLOB))
        .add(f(BLOB, f(BLOB)))
        .build();
    ImmutableList<Type> unchanged = ImmutableList.<Type>builder()
        .addAll(map(unchangedByStripping, t -> t))
        .addAll(map(unchangedByStripping, t -> a(t)))
        .addAll(map(unchangedByStripping, t -> a(a(t))))
        .build();

    return ImmutableList.<Arguments>builder()
        .addAll(map(unchanged, t -> Arguments.of(t, t)))
        .add(Arguments.of(f(BLOB, item(BLOB, "p")), f(BLOB, BLOB)))
        .add(Arguments.of(f(f(BLOB, item(BLOB, "p")), BLOB), f(f(BLOB, BLOB), BLOB)))
        .add(Arguments.of(f(BLOB, f(BLOB, item(BLOB, "p"))), f(BLOB, f(BLOB, BLOB))))
        .add(Arguments.of(a(f(BLOB, item(BLOB, "p"))), a(f(BLOB, BLOB))))
        .build();
  }

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignmentSpec spec) {
    Type target = spec.target().strippedType();
    Type source = spec.source().strippedType();
    assertThat(typing().isAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  public static List<TestedAssignmentSpec> isAssignable_test_data() {
    return TestedAssignmentSpec.assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignmentSpec spec) {
    Type target = spec.target().strippedType();
    Type source = spec.source().strippedType();
    assertThat(typing().isParamAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  public static List<TestedAssignmentSpec> isParamAssignable_test_data() {
    return TestedAssignmentSpec.parameter_assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("inferVariableBounds_test_data")
  public void inferVariableBounds(Type type, Type assigned, BoundsMap expected) {
    assertThat(typing().inferVariableBounds(type, assigned, lower()))
        .isEqualTo(expected);
  }

  public static List<Arguments> inferVariableBounds_test_data() {
    Side LOWER = new TestingContext().lower();
    Side UPPER = new TestingContext().upper();
    var r = new ArrayList<Arguments>();
    for (Type type : concat(ELEMENTARY_TYPES, X)) {
      if (type instanceof NothingType) {
        // arrays
        r.add(arguments(A, NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(A, a(NOTHING), bm(A, LOWER, a(NOTHING))));
        r.add(arguments(A, a(a(NOTHING)), bm(A, LOWER, a(a(NOTHING)))));

        r.add(arguments(a(A), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(a(A), a(NOTHING), bm(A, LOWER, NOTHING)));
        r.add(arguments(a(A), a(a(NOTHING)), bm(A, LOWER, a(NOTHING))));

        r.add(arguments(a(a(A)), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(a(a(A)), a(NOTHING), bm(A, LOWER, NOTHING)));
        r.add(arguments(a(a(A)), a(a(NOTHING)), bm(A, LOWER, NOTHING)));

        // functions
        r.add(arguments(f(A), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(f(A)), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(f(f(A))), NOTHING, bm(A, LOWER, NOTHING)));

        r.add(arguments(f(BOOL, A), NOTHING, bm(A, UPPER, ANY)));
        r.add(arguments(f(BOOL, f(A)), NOTHING, bm(A, UPPER, ANY)));
        r.add(arguments(f(BOOL, f(f(A))), NOTHING, bm(A, UPPER, ANY)));

        r.add(arguments(f(BOOL, f(BLOB, A)), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, f(BLOB, f(A))), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, f(BLOB, f(f(A)))), NOTHING, bm(A, LOWER, NOTHING)));

        // arrays + functions
        r.add(arguments(a(f(A)), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(a(f(STRING, A)), NOTHING, bm(A, UPPER, ANY)));

        r.add(arguments(f(a(A)), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, a(A)), NOTHING, bm(A, UPPER, ANY)));
      } else {
        // arrays
        r.add(arguments(A, type, bm(A, LOWER, type)));
        r.add(arguments(A, a(type), bm(A, LOWER, a(type))));
        r.add(arguments(A, a(a(type)), bm(A, LOWER, a(a(type)))));

        r.add(arguments(a(A), type, bm()));
        r.add(arguments(a(A), a(type), bm(A, LOWER, type)));
        r.add(arguments(a(A), a(a(type)), bm(A, LOWER, a(type))));

        r.add(arguments(a(a(A)), type, bm()));
        r.add(arguments(a(a(A)), a(type), bm()));
        r.add(arguments(a(a(A)), a(a(type)), bm(A, LOWER, type)));

        // functions
        r.add(arguments(f(A), type, bm()));
        r.add(arguments(f(A), f(type), bm(A, LOWER, type)));
        r.add(arguments(f(A), f(f(type)), bm(A, LOWER, f(type))));
        r.add(arguments(f(A), f(f(f(type))), bm(A, LOWER, f(f(type)))));

        r.add(arguments(f(f(A)), type, bm()));
        r.add(arguments(f(f(A)), f(type), bm()));
        r.add(arguments(f(f(A)), f(f(type)), bm(A, LOWER, type)));
        r.add(arguments(f(f(A)), f(f(f(type))), bm(A, LOWER, f(type))));

        r.add(arguments(f(f(f(A))), type, bm()));
        r.add(arguments(f(f(f(A))), f(type), bm()));
        r.add(arguments(f(f(f(A))), f(f(type)), bm()));
        r.add(arguments(f(f(f(A))), f(f(f(type))), bm(A, LOWER, type)));

        r.add(arguments(f(BOOL, A), f(BOOL, type), bm(A, UPPER, type)));
        r.add(arguments(f(BOOL, f(A)), f(BOOL, f(type)), bm(A, UPPER, type)));
        r.add(arguments(f(BOOL, f(f(A))), f(BOOL, f(f(type))), bm(A, UPPER, type)));

        r.add(arguments(f(BOOL, f(BLOB, A)), f(BOOL, f(BLOB, type)), bm(A, LOWER, type)));
        r.add(arguments(f(BOOL, f(BLOB, f(A))), f(BOOL, f(BLOB, f(type))), bm(A, LOWER, type)));
        r.add(arguments(f(BOOL, f(BLOB, f(f(A)))), f(BOOL, f(BLOB, f(f(type)))), bm(A, LOWER, type)));

        // arrays + functions
        r.add(arguments(a(f(A)), a(f(type)), bm(A, LOWER, type)));
        r.add(arguments(a(f(BOOL, A)), a(f(BOOL, type)), bm(A, UPPER, type)));

        r.add(arguments(f(a(A)), f(a(type)), bm(A, LOWER, type)));
        r.add(arguments(f(BOOL, a(A)), f(BOOL, a(type)), bm(A, UPPER, type)));
      }
    }
    return r;
  }

  @ParameterizedTest
  @MethodSource("mapVariables_test_data")
  public void mapVariables(Type type, BoundsMap boundsMap, Type expected) {
    assertThat(typing().mapVariables(type, boundsMap, lower()))
        .isEqualTo(expected);
  }

  public static List<Arguments> mapVariables_test_data() {
    Side LOWER = new TestingContext().lower();
    Side UPPER = new TestingContext().upper();

    var result = new ArrayList<Arguments>();
    for (Type type : ALL_TESTED_TYPES) {
      result.add(arguments(X, bm(X, LOWER, type), type));
      result.add(arguments(a(X), bm(X, LOWER, type), a(type)));
      result.add(arguments(X, bm(X, LOWER, a(type)), a(type)));
      result.add(arguments(a(X), bm(X, LOWER, a(type)), a(a(type))));
    }
    for (Type newA : ALL_TESTED_TYPES) {
      for (Type newB : ALL_TESTED_TYPES) {
        result.add(arguments(f(A, B), bm(A, LOWER, newA, B, UPPER, newB), f(newA, newB)));
      }
    }
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, bm(), type));
      result.add(arguments(a(a(type)), bm(), a(a(type))));

      result.add(arguments(f(type), bm(), f(type)));
      result.add(arguments(f(BOOL, type), bm(), f(BOOL, type)));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_cases")
  public void merge_up_wide_graph(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, upper());
  }

  public static Collection<Arguments> merge_up_wide_graph_cases() {
    return buildWideGraph()
        .buildTestCases(NOTHING);
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_cases")
  public void merge_up_deep_graph(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, upper());
  }

  public static Collection<Arguments> merge_up_deep_graph_cases() {
    return buildGraph(list(BLOB), 2)
        .buildTestCases(NOTHING);
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_cases")
  public void merge_down_wide_graph(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, lower());
  }

  public static Collection<Arguments> merge_down_wide_graph_cases() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(ANY);
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_cases")
  public void merge_down_deep_graph(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, lower());
  }

  public static Collection<Arguments> merge_down_deep_graph_cases() {
    return buildGraph(list(BLOB), 2)
        .inverse()
        .buildTestCases(ANY);
  }

  private void testMergeBothWays(Type type1, Type type2, Type expected, Side direction) {
    assertThat(typing().merge(type1, type2, direction))
        .isEqualTo(expected);
    assertThat(typing().merge(type2, type1, direction))
        .isEqualTo(expected);
  }

  private static TestingTypeGraph buildWideGraph() {
    if (BASE_TYPES.size() != 5) {
      throw new RuntimeException("Add missing type to list below.");
    }
    return buildGraph(list(A, B, BLOB, BOOL, DATA, INT, FLAG, PERSON, STRING), 1);
  }
}
