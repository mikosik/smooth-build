package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTsS.A;
import static org.smoothbuild.lang.base.type.TestingTsS.ALL_TESTED_TYPES;
import static org.smoothbuild.lang.base.type.TestingTsS.ANY;
import static org.smoothbuild.lang.base.type.TestingTsS.B;
import static org.smoothbuild.lang.base.type.TestingTsS.BASE_TYPES;
import static org.smoothbuild.lang.base.type.TestingTsS.BLOB;
import static org.smoothbuild.lang.base.type.TestingTsS.BOOL;
import static org.smoothbuild.lang.base.type.TestingTsS.DATA;
import static org.smoothbuild.lang.base.type.TestingTsS.ELEMENTARY_TYPES;
import static org.smoothbuild.lang.base.type.TestingTsS.FLAG;
import static org.smoothbuild.lang.base.type.TestingTsS.INT;
import static org.smoothbuild.lang.base.type.TestingTsS.LOWER;
import static org.smoothbuild.lang.base.type.TestingTsS.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTsS.PERSON;
import static org.smoothbuild.lang.base.type.TestingTsS.STRING;
import static org.smoothbuild.lang.base.type.TestingTsS.TYPING;
import static org.smoothbuild.lang.base.type.TestingTsS.UPPER;
import static org.smoothbuild.lang.base.type.TestingTsS.X;
import static org.smoothbuild.lang.base.type.TestingTsS.a;
import static org.smoothbuild.lang.base.type.TestingTsS.bm;
import static org.smoothbuild.lang.base.type.TestingTsS.f;
import static org.smoothbuild.lang.base.type.TestingTsS.oneSideBound;
import static org.smoothbuild.lang.base.type.TestingTypeGraph.buildGraph;
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
import org.smoothbuild.lang.base.type.api.Sides.Side;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.impl.NothingTS;
import org.smoothbuild.lang.base.type.impl.TypeS;

public class TypingTest {
  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void contains(TypeS type, TypeS contained, boolean expected) {
    assertThat(TYPING.contains(type, contained))
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
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(TYPING.isAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  public static List<TestedAssignmentSpec> isAssignable_test_data() {
    return TestedAssignmentSpec.assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("isParamAssignable_test_data")
  public void isParamAssignable(TestedAssignmentSpec spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(TYPING.isParamAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  public static List<TestedAssignmentSpec> isParamAssignable_test_data() {
    return TestedAssignmentSpec.param_assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("inferVarBounds_test_data")
  public void inferVarBounds(TypeS type, TypeS assigned, BoundsMap<TypeS> expected) {
    assertThat(TYPING.inferVarBounds(type, assigned, LOWER))
        .isEqualTo(expected);
  }

  public static List<Arguments> inferVarBounds_test_data() {
    var r = new ArrayList<Arguments>();
    for (TypeS type : concat(ELEMENTARY_TYPES, X)) {
      if (type instanceof NothingTS) {
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

        // funcs
        r.add(arguments(f(A), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(f(A)), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(f(f(A))), NOTHING, bm(A, LOWER, NOTHING)));

        r.add(arguments(f(BOOL, A), NOTHING, bm(A, UPPER, ANY)));
        r.add(arguments(f(BOOL, f(A)), NOTHING, bm(A, UPPER, ANY)));
        r.add(arguments(f(BOOL, f(f(A))), NOTHING, bm(A, UPPER, ANY)));

        r.add(arguments(f(BOOL, f(BLOB, A)), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, f(BLOB, f(A))), NOTHING, bm(A, LOWER, NOTHING)));
        r.add(arguments(f(BOOL, f(BLOB, f(f(A)))), NOTHING, bm(A, LOWER, NOTHING)));

        // arrays + funcs
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

        // funcs
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

        // arrays + funcs
        r.add(arguments(a(f(A)), a(f(type)), bm(A, LOWER, type)));
        r.add(arguments(a(f(BOOL, A)), a(f(BOOL, type)), bm(A, UPPER, type)));

        r.add(arguments(f(a(A)), f(a(type)), bm(A, LOWER, type)));
        r.add(arguments(f(BOOL, a(A)), f(BOOL, a(type)), bm(A, UPPER, type)));
      }
    }
    return r;
  }

  @ParameterizedTest
  @MethodSource("mapVars_test_data")
  public void mapVars(TypeS type, BoundsMap<TypeS> boundsMap, Type expected) {
    assertThat(TYPING.mapVars(type, boundsMap, LOWER))
        .isEqualTo(expected);
  }

  public static List<Arguments> mapVars_test_data() {
    var result = new ArrayList<Arguments>();
    for (TypeS type : ALL_TESTED_TYPES) {
      result.add(arguments(X, bm(), X));
      result.add(arguments(a(X), bm(), a(X)));
      result.add(arguments(X, bm(X, LOWER, type), type));
      result.add(arguments(a(X), bm(X, LOWER, type), a(type)));
      result.add(arguments(X, bm(X, LOWER, a(type)), a(type)));
      result.add(arguments(a(X), bm(X, LOWER, a(type)), a(a(type))));
    }
    for (TypeS newA : ALL_TESTED_TYPES) {
      for (TypeS newB : ALL_TESTED_TYPES) {
        result.add(arguments(f(A, B), bm(A, LOWER, newA, B, UPPER, newB), f(newA, newB)));
      }
    }
    for (TypeS type : ELEMENTARY_TYPES) {
      result.add(arguments(type, bm(), type));
      result.add(arguments(a(a(type)), bm(), a(a(type))));

      result.add(arguments(f(type), bm(), f(type)));
      result.add(arguments(f(BOOL, type), bm(), f(BOOL, type)));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("merge_up_wide_graph_cases")
  public void merge_up_wide_graph(TypeS type1, TypeS type2, TypeS expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  public static Collection<Arguments> merge_up_wide_graph_cases() {
    return buildWideGraph()
        .buildTestCases(NOTHING);
  }

  @ParameterizedTest
  @MethodSource("merge_up_deep_graph_cases")
  public void merge_up_deep_graph(TypeS type1, TypeS type2, TypeS expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  public static Collection<Arguments> merge_up_deep_graph_cases() {
    return buildGraph(list(BLOB), 2)
        .buildTestCases(NOTHING);
  }

  @ParameterizedTest
  @MethodSource("merge_down_wide_graph_cases")
  public void merge_down_wide_graph(TypeS type1, TypeS type2, TypeS expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  public static Collection<Arguments> merge_down_wide_graph_cases() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(ANY);
  }

  @ParameterizedTest
  @MethodSource("merge_down_deep_graph_cases")
  public void merge_down_deep_graph(TypeS type1, TypeS type2, Type expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  public static Collection<Arguments> merge_down_deep_graph_cases() {
    return buildGraph(list(BLOB), 2)
        .inverse()
        .buildTestCases(ANY);
  }

  private void testMergeBothWays(TypeS type1, TypeS type2, Type expected, Side<TypeS> direction) {
    assertThat(TYPING.merge(type1, type2, direction))
        .isEqualTo(expected);
    assertThat(TYPING.merge(type2, type1, direction))
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
    public void var_with_one_lower_bound() {
      var bounds = oneSideBound(LOWER, STRING);
      assertThat(bounds.upper()).isEqualTo(ANY);
      assertThat(bounds.lower()).isEqualTo(STRING);
    }

    @Test
    public void var_with_one_upper_bound() {
      var bounds = oneSideBound(UPPER, STRING);
      assertThat(bounds.upper()).isEqualTo(STRING);
      assertThat(bounds.lower()).isEqualTo(NOTHING);
    }

    @Test
    public void var_with_two_lower_bounds() {
      var bounds = TYPING.merge(
          oneSideBound(LOWER, STRING),
          oneSideBound(LOWER, BOOL));
      assertThat(bounds.upper()).isEqualTo(ANY);
      assertThat(bounds.lower()).isEqualTo(ANY);
    }

    @Test
    public void var_with_two_upper_bounds() {
      var bounds = TYPING.merge(
          oneSideBound(UPPER, STRING),
          oneSideBound(UPPER, BOOL));
      assertThat(bounds.upper()).isEqualTo(NOTHING);
      assertThat(bounds.lower()).isEqualTo(NOTHING);
    }
  }
}
