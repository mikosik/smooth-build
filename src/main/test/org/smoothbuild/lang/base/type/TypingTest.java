package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypeGraph.buildGraph;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ALL_TESTED_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BASE_TYPES;
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
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.testing.TestingContextImpl;

public class TypingTest extends TestingContextImpl {
  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void contains(Type type, Type contained, boolean expected) {
    assertThat(typing().contains(type, contained))
        .isEqualTo(expected);
  }

  public static List<Arguments> contains_test_data() {
    return list(
        arguments(ANY, ANY, true),
        arguments(ANY, BLOB, false),
        arguments(ANY, BOOL, false),
        arguments(ANY, INT, false),
        arguments(ANY, NOTHING, false),
        arguments(ANY, STRING, false),
        arguments(ANY, PERSON, false),

        arguments(ANY, a(ANY), false),
        arguments(ANY, a(BLOB), false),
        arguments(ANY, a(BOOL), false),
        arguments(ANY, a(INT), false),
        arguments(ANY, a(NOTHING), false),
        arguments(ANY, a(STRING), false),
        arguments(ANY, a(PERSON), false),

        arguments(ANY, f(ANY), false),
        arguments(ANY, f(BLOB), false),
        arguments(ANY, f(BOOL), false),
        arguments(ANY, f(INT), false),
        arguments(ANY, f(NOTHING), false),
        arguments(ANY, f(STRING), false),
        arguments(ANY, f(PERSON), false),

        arguments(BLOB, ANY, false),
        arguments(BLOB, BLOB, true),
        arguments(BLOB, BOOL, false),
        arguments(BLOB, INT, false),
        arguments(BLOB, NOTHING, false),
        arguments(BLOB, STRING, false),
        arguments(BLOB, PERSON, false),

        arguments(BLOB, a(ANY), false),
        arguments(BLOB, a(BLOB), false),
        arguments(BLOB, a(BOOL), false),
        arguments(BLOB, a(INT), false),
        arguments(BLOB, a(NOTHING), false),
        arguments(BLOB, a(STRING), false),
        arguments(BLOB, a(PERSON), false),

        arguments(BLOB, f(ANY), false),
        arguments(BLOB, f(BLOB), false),
        arguments(BLOB, f(BOOL), false),
        arguments(BLOB, f(INT), false),
        arguments(BLOB, f(NOTHING), false),
        arguments(BLOB, f(STRING), false),
        arguments(BLOB, f(PERSON), false),

        arguments(a(BLOB), ANY, false),
        arguments(a(BLOB), BLOB, true),
        arguments(a(BLOB), BOOL, false),
        arguments(a(BLOB), INT, false),
        arguments(a(BLOB), NOTHING, false),
        arguments(a(BLOB), STRING, false),
        arguments(a(BLOB), PERSON, false),

        arguments(a(BLOB), a(ANY), false),
        arguments(a(BLOB), a(BLOB), true),
        arguments(a(BLOB), a(BOOL), false),
        arguments(a(BLOB), a(INT), false),
        arguments(a(BLOB), a(NOTHING), false),
        arguments(a(BLOB), a(STRING), false),
        arguments(a(BLOB), a(PERSON), false),

        arguments(a(BLOB), f(ANY), false),
        arguments(a(BLOB), f(BLOB), false),
        arguments(a(BLOB), f(BOOL), false),
        arguments(a(BLOB), f(INT), false),
        arguments(a(BLOB), f(NOTHING), false),
        arguments(a(BLOB), f(STRING), false),
        arguments(a(BLOB), f(PERSON), false),

        arguments(f(BLOB), ANY, false),
        arguments(f(BLOB), BLOB, true),
        arguments(f(BLOB), BOOL, false),
        arguments(f(BLOB), INT, false),
        arguments(f(BLOB), NOTHING, false),
        arguments(f(BLOB), STRING, false),
        arguments(f(BLOB), PERSON, false),

        arguments(f(BLOB), a(ANY), false),
        arguments(f(BLOB), a(BLOB), false),
        arguments(f(BLOB), a(BOOL), false),
        arguments(f(BLOB), a(INT), false),
        arguments(f(BLOB), a(NOTHING), false),
        arguments(f(BLOB), a(STRING), false),
        arguments(f(BLOB), a(PERSON), false),

        arguments(f(BLOB), f(ANY), false),
        arguments(f(BLOB), f(BLOB), true),
        arguments(f(BLOB), f(BOOL), false),
        arguments(f(BLOB), f(INT), false),
        arguments(f(BLOB), f(NOTHING), false),
        arguments(f(BLOB), f(STRING), false),
        arguments(f(BLOB), f(PERSON), false),

        arguments(f(STRING, BLOB), ANY, false),
        arguments(f(STRING, BLOB), BLOB, true),
        arguments(f(STRING, BLOB), BOOL, false),
        arguments(f(STRING, BLOB), INT, false),
        arguments(f(STRING, BLOB), NOTHING, false),
        arguments(f(STRING, BLOB), STRING, true),
        arguments(f(STRING, BLOB), PERSON, false),

        arguments(f(STRING, BLOB), a(ANY), false),
        arguments(f(STRING, BLOB), a(BLOB), false),
        arguments(f(STRING, BLOB), a(BOOL), false),
        arguments(f(STRING, BLOB), a(INT), false),
        arguments(f(STRING, BLOB), a(NOTHING), false),
        arguments(f(STRING, BLOB), a(STRING), false),
        arguments(f(STRING, BLOB), a(PERSON), false),

        arguments(f(STRING, BLOB), f(ANY), false),
        arguments(f(STRING, BLOB), f(BLOB), false),
        arguments(f(STRING, BLOB), f(BOOL), false),
        arguments(f(STRING, BLOB), f(INT), false),
        arguments(f(STRING, BLOB), f(NOTHING), false),
        arguments(f(STRING, BLOB), f(STRING), false),
        arguments(f(STRING, BLOB), f(PERSON), false),

        arguments(f(STRING, BLOB), f(STRING, BLOB), true)
    );
  }

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignmentSpec spec) {
    Type target = spec.target().type();
    Type source = spec.source().type();
    assertThat(typing().isAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  public static List<TestedAssignmentSpec> isAssignable_test_data() {
    return TestedAssignmentSpec.assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignmentSpec spec) {
    Type target = spec.target().type();
    Type source = spec.source().type();
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
    TestingContextImpl tc = new TestingContextImpl();
    Side LOWER = tc.lower();
    Side UPPER = tc.upper();
    var r = new ArrayList<Arguments>();
    for (Type type : concat(ELEMENTARY_TYPES, X)) {
      if (type instanceof NothingType) {
        // arrays
        r.add(arguments(A, NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(A, a(NOTHING), tc.bm(A, LOWER, a(NOTHING))));
        r.add(arguments(A, a(a(NOTHING)), tc.bm(A, LOWER, a(a(NOTHING)))));

        r.add(arguments(a(A), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(a(A), a(NOTHING), tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(a(A), a(a(NOTHING)), tc.bm(A, LOWER, a(NOTHING))));

        r.add(arguments(a(a(A)), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(a(a(A)), a(NOTHING), tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(a(a(A)), a(a(NOTHING)), tc.bm(A, LOWER, NOTHING)));

        // functions
        r.add(arguments(f(A), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(f(f(A)), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(f(f(f(A))), NOTHING, tc.bm(A, LOWER, NOTHING)));

        r.add(arguments(f(BOOL, A), NOTHING, tc.bm(A, UPPER, ANY)));
        r.add(arguments(f(BOOL, f(A)), NOTHING, tc.bm(A, UPPER, ANY)));
        r.add(arguments(f(BOOL, f(f(A))), NOTHING, tc.bm(A, UPPER, ANY)));

        r.add(arguments(f(BOOL, f(BLOB, A)), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, f(BLOB, f(A))), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, f(BLOB, f(f(A)))), NOTHING, tc.bm(A, LOWER, NOTHING)));

        // arrays + functions
        r.add(arguments(a(f(A)), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(a(f(STRING, A)), NOTHING, tc.bm(A, UPPER, ANY)));

        r.add(arguments(f(a(A)), NOTHING, tc.bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, a(A)), NOTHING, tc.bm(A, UPPER, ANY)));
      } else {
        // arrays
        r.add(arguments(A, type, tc.bm(A, LOWER, type)));
        r.add(arguments(A, a(type), tc.bm(A, LOWER, a(type))));
        r.add(arguments(A, a(a(type)), tc.bm(A, LOWER, a(a(type)))));

        r.add(arguments(a(A), type, tc.bm()));
        r.add(arguments(a(A), a(type), tc.bm(A, LOWER, type)));
        r.add(arguments(a(A), a(a(type)), tc.bm(A, LOWER, a(type))));

        r.add(arguments(a(a(A)), type, tc.bm()));
        r.add(arguments(a(a(A)), a(type), tc.bm()));
        r.add(arguments(a(a(A)), a(a(type)), tc.bm(A, LOWER, type)));

        // functions
        r.add(arguments(f(A), type, tc.bm()));
        r.add(arguments(f(A), f(type), tc.bm(A, LOWER, type)));
        r.add(arguments(f(A), f(f(type)), tc.bm(A, LOWER, f(type))));
        r.add(arguments(f(A), f(f(f(type))), tc.bm(A, LOWER, f(f(type)))));

        r.add(arguments(f(f(A)), type, tc.bm()));
        r.add(arguments(f(f(A)), f(type), tc.bm()));
        r.add(arguments(f(f(A)), f(f(type)), tc.bm(A, LOWER, type)));
        r.add(arguments(f(f(A)), f(f(f(type))), tc.bm(A, LOWER, f(type))));

        r.add(arguments(f(f(f(A))), type, tc.bm()));
        r.add(arguments(f(f(f(A))), f(type), tc.bm()));
        r.add(arguments(f(f(f(A))), f(f(type)), tc.bm()));
        r.add(arguments(f(f(f(A))), f(f(f(type))), tc.bm(A, LOWER, type)));

        r.add(arguments(f(BOOL, A), f(BOOL, type), tc.bm(A, UPPER, type)));
        r.add(arguments(f(BOOL, f(A)), f(BOOL, f(type)), tc.bm(A, UPPER, type)));
        r.add(arguments(f(BOOL, f(f(A))), f(BOOL, f(f(type))), tc.bm(A, UPPER, type)));

        r.add(arguments(f(BOOL, f(BLOB, A)), f(BOOL, f(BLOB, type)), tc.bm(A, LOWER, type)));
        r.add(arguments(f(BOOL, f(BLOB, f(A))), f(BOOL, f(BLOB, f(type))), tc.bm(A, LOWER, type)));
        r.add(arguments(f(BOOL, f(BLOB, f(f(A)))), f(BOOL, f(BLOB, f(f(type)))), tc.bm(A, LOWER, type)));

        // arrays + functions
        r.add(arguments(a(f(A)), a(f(type)), tc.bm(A, LOWER, type)));
        r.add(arguments(a(f(BOOL, A)), a(f(BOOL, type)), tc.bm(A, UPPER, type)));

        r.add(arguments(f(a(A)), f(a(type)), tc.bm(A, LOWER, type)));
        r.add(arguments(f(BOOL, a(A)), f(BOOL, a(type)), tc.bm(A, UPPER, type)));
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
    TestingContextImpl tc = new TestingContextImpl();
    Side LOWER = tc.lower();
    Side UPPER = tc.upper();

    var result = new ArrayList<Arguments>();
    for (Type type : ALL_TESTED_TYPES) {
      result.add(arguments(X, tc.bm(X, LOWER, type), type));
      result.add(arguments(a(X), tc.bm(X, LOWER, type), a(type)));
      result.add(arguments(X, tc.bm(X, LOWER, a(type)), a(type)));
      result.add(arguments(a(X), tc.bm(X, LOWER, a(type)), a(a(type))));
    }
    for (Type newA : ALL_TESTED_TYPES) {
      for (Type newB : ALL_TESTED_TYPES) {
        result.add(arguments(f(A, B), tc.bm(A, LOWER, newA, B, UPPER, newB), f(newA, newB)));
      }
    }
    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, tc.bm(), type));
      result.add(arguments(a(a(type)), tc.bm(), a(a(type))));

      result.add(arguments(f(type), tc.bm(), f(type)));
      result.add(arguments(f(BOOL, type), tc.bm(), f(BOOL, type)));
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
  @Nested
  class _merge_bounds {
    @Test
    public void variable_with_one_lower_bound() {
      var bounds = typing().oneSideBound(lower(), STRING);
      assertThat(bounds.upper()).isEqualTo(ANY);
      assertThat(bounds.lower()).isEqualTo(STRING);
    }

    @Test
    public void variable_with_one_upper_bound() {
      var bounds = typing().oneSideBound(upper(), STRING);
      assertThat(bounds.upper()).isEqualTo(STRING);
      assertThat(bounds.lower()).isEqualTo(NOTHING);
    }

    @Test
    public void variable_with_two_lower_bounds() {
      var bounds = typing().merge(
          typing().oneSideBound(lower(), STRING),
          typing().oneSideBound(lower(), BOOL));
      assertThat(bounds.upper()).isEqualTo(ANY);
      assertThat(bounds.lower()).isEqualTo(ANY);
    }

    @Test
    public void variable_with_two_upper_bounds() {
      var bounds = typing().merge(
          typing().oneSideBound(upper(), STRING),
          typing().oneSideBound(upper(), BOOL));
      assertThat(bounds.upper()).isEqualTo(NOTHING);
      assertThat(bounds.lower()).isEqualTo(NOTHING);
    }
  }
}
