package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.TestingItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.TestingTypeGraph.buildGraph;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ALL_TESTED_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_HEAD_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_LENGTH_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.C;
import static org.smoothbuild.lang.base.type.TestingTypes.D;
import static org.smoothbuild.lang.base.type.TestingTypes.DATA;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.FLAG;
import static org.smoothbuild.lang.base.type.TestingTypes.IDENTITY_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.INT;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON_GETTER_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON_MAP_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING_GETTER_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING_MAP_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.X;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.f;
import static org.smoothbuild.lang.base.type.TestingTypes.item;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.lang.base.type.Types.INFERABLE_BASE_TYPES;
import static org.smoothbuild.lang.base.type.Types.functionT;
import static org.smoothbuild.lang.base.type.Types.structT;
import static org.smoothbuild.lang.base.type.constraint.TestingBoundsMap.bm;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.Sets.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class TypeTest {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(INFERABLE_BASE_TYPES)
        .hasSize(6);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void name(Type type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Type type, String name) {
    assertThat(type.q())
        .isEqualTo("`" + name + "`");
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Type type, String name) {
    assertThat(type.toString())
        .isEqualTo("Type(`" + name + "`)");
  }

  public static Stream<Arguments> names() {
    return Stream.of(
        arguments(A, "A"),
        arguments(ANY, "Any"),
        arguments(BLOB, "Blob"),
        arguments(BOOL, "Bool"),
        arguments(INT, "Int"),
        arguments(NOTHING, "Nothing"),
        arguments(STRING, "String"),
        arguments(PERSON, "Person"),

        arguments(a(A), "[A]"),
        arguments(a(ANY), "[Any]"),
        arguments(a(BLOB), "[Blob]"),
        arguments(a(BOOL), "[Bool]"),
        arguments(a(INT), "[Int]"),
        arguments(a(NOTHING), "[Nothing]"),
        arguments(a(STRING), "[String]"),
        arguments(a(PERSON), "[Person]"),

        arguments(a(a(A)), "[[A]]"),
        arguments(a(a(ANY)), "[[Any]]"),
        arguments(a(a(BLOB)), "[[Blob]]"),
        arguments(a(a(BOOL)), "[[Bool]]"),
        arguments(a(a(INT)), "[[Int]]"),
        arguments(a(a(NOTHING)), "[[Nothing]]"),
        arguments(a(a(STRING)), "[[String]]"),
        arguments(a(a(PERSON)), "[[Person]]"),

        arguments(ARRAY_HEAD_FUNCTION, "A([A])"),
        arguments(ARRAY_LENGTH_FUNCTION, "String([A])"),
        arguments(IDENTITY_FUNCTION, "A(A)"),
        arguments(PERSON_GETTER_FUNCTION, "Person()"),
        arguments(PERSON_MAP_FUNCTION, "Person(Person)"),
        arguments(STRING_GETTER_FUNCTION, "String()"),
        arguments(STRING_MAP_FUNCTION, "String(String)"),
        arguments(functionT(BOOL, list(
            new ItemSignature(BLOB, Optional.of("name"), Optional.empty()))), "Bool(Blob name)")
    );
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(Type type, boolean expected) {
    assertThat(type.isPolytype())
        .isEqualTo(expected);
  }

  public static List<Arguments> isPolytype_test_data() {
    var result = new ArrayList<Arguments>();
    result.add(arguments(A, true));
    result.add(arguments(a(A), true));
    result.add(arguments(a(a(A)), true));

    result.add(arguments(f(A), true));
    result.add(arguments(f(f(A)), true));
    result.add(arguments(f(f(f(A))), true));

    result.add(arguments(f(BOOL, A), true));
    result.add(arguments(f(BOOL, f(A)), true));
    result.add(arguments(f(BOOL, f(f(A))), true));

    result.add(arguments(f(BOOL, f(BLOB, A)), true));
    result.add(arguments(f(BOOL, f(BLOB, f(A))), true));
    result.add(arguments(f(BOOL, f(BLOB, f(f(A)))), true));

    for (Type type : ELEMENTARY_TYPES) {
      result.add(arguments(type, false));
      result.add(arguments(a(type), false));
      result.add(arguments(a(a(type)), false));

      result.add(arguments(f(type), false));
      result.add(arguments(f(BOOL, type), false));
      result.add(arguments(f(BOOL, f(BLOB, type)), false));
    }

    return result;
  }

  @ParameterizedTest
  @MethodSource("contains_test_data")
  public void contains(Type type, Type contained, boolean expected) {
    assertThat(type.contains(contained))
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
  @MethodSource("variables_test_data")
  public void variables(Type type, Set<Variable> variables) {
    assertThat(type.variables())
        .containsExactlyElementsIn(variables)
        .inOrder();
  }

  public static List<Arguments> variables_test_data() {
    return list(
        arguments(ANY, set()),
        arguments(BLOB, set()),
        arguments(BOOL, set()),
        arguments(INT, set()),
        arguments(NOTHING, set()),
        arguments(STRING, set()),
        arguments(PERSON, set()),

        arguments(a(ANY), set()),
        arguments(a(BLOB), set()),
        arguments(a(BOOL), set()),
        arguments(a(INT), set()),
        arguments(a(NOTHING), set()),
        arguments(a(STRING), set()),
        arguments(a(PERSON), set()),

        arguments(f(STRING), set()),
        arguments(f(STRING, BOOL), set()),

        arguments(A, set(A)),
        arguments(a(A), set(A)),
        arguments(a(a(A)), set(A)),
        arguments(f(A), set(A)),
        arguments(f(A, STRING), set(A)),
        arguments(f(STRING, A), set(A)),
        arguments(f(B, A), set(A, B)),
        arguments(f(D, C, B, A), set(A, B, C, D))
    );
  }

  @ParameterizedTest
  @MethodSource("isAssignableFrom_test_data")
  public void isAssignableFrom(TestedAssignmentSpec spec) {
    assertThat(spec.target().strippedType().isAssignableFrom(spec.source().strippedType()))
        .isEqualTo(spec.allowed());
  }

  public static List<TestedAssignmentSpec> isAssignableFrom_test_data() {
    return TestedAssignmentSpec.assignment_test_specs(true);
  }

  @ParameterizedTest
  @MethodSource("mergeWith_upper_direction_wide_graph_cases")
  public void mergeWith_upper_direction_wide_graph(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  public static Collection<Arguments> mergeWith_upper_direction_wide_graph_cases() {
    return buildWideGraph()
        .buildTestCases(NOTHING);
  }

  @ParameterizedTest
  @MethodSource("mergeWith_upper_direction_deep_graph_cases")
  public void mergeWith_upper_direction_deep_graph(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, UPPER);
  }

  public static Collection<Arguments> mergeWith_upper_direction_deep_graph_cases() {
    return buildGraph(list(BLOB), 2)
        .buildTestCases(NOTHING);
  }

  @ParameterizedTest
  @MethodSource("mergeWith_lower_direction_wide_graph_cases")
  public void mergeWith_lower_direction_wide_graph_cases(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  public static Collection<Arguments> mergeWith_lower_direction_wide_graph_cases() {
    return buildWideGraph()
        .inverse()
        .buildTestCases(ANY);
  }

  @ParameterizedTest
  @MethodSource("mergeWith_lower_direction_deep_graph_cases")
  public void mergeWith_lower_direction_deep_graph_cases(Type type1, Type type2, Type expected) {
    testMergeBothWays(type1, type2, expected, LOWER);
  }

  public static Collection<Arguments> mergeWith_lower_direction_deep_graph_cases() {
    return buildGraph(list(BLOB), 2)
        .inverse()
        .buildTestCases(ANY);
  }

  private static void testMergeBothWays(Type type1, Type type2, Type expected, Side upper) {
    assertThat(type1.mergeWith(type2, upper))
        .isEqualTo(expected);
    assertThat(type2.mergeWith(type1, upper))
        .isEqualTo(expected);
  }

  private static TestingTypeGraph buildWideGraph() {
    if (BASE_TYPES.size() != 5) {
      throw new RuntimeException("Add missing type to list below.");
    }
    return buildGraph(list(A, B, BLOB, BOOL, DATA, INT, FLAG, PERSON, STRING), 1);
  }

  @ParameterizedTest
  @MethodSource("elemType_test_data")
  public void elemType(ArrayType type, Type expected) {
    assertThat(type.elemType())
        .isEqualTo(expected);
  }

  public static List<Arguments> elemType_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : concat(ELEMENTARY_TYPES, X)) {
      result.add(arguments(a(type), type));
      result.add(arguments(a(a(type)), a(type)));
      result.add(arguments(a(a(a(type))), a(a(type))));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("strip_test_data")
  public void strip(Type type, Type expected) {
    assertThat(type.strip())
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

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<Type> types = ImmutableList.<Type>builder()
        .addAll(ELEMENTARY_TYPES)
        .add(A)
        .add(B)
        .add(structT("MyStruct", list()))
        .add(structT("MyStruct", list(itemSignature(BOOL, "field"))))
        .add(structT("MyStruct2", list(itemSignature(BOOL, "field"))))
        .add(structT("MyStruct", list(itemSignature(STRING, "field"))))
        .add(structT("MyStruct", list(itemSignature(BOOL, "field2"))))
        .build();
    for (Type type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(a(type), a(type));
      equalsTester.addEqualityGroup(a(a(type)), a(a(type)));
    }
    equalsTester.testEquals();
  }
}
