package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingItemSignature.itemSignature;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_HEAD_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_LENGTH_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.C;
import static org.smoothbuild.lang.base.type.TestingTypes.D;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.IDENTITY_FUNCTION;
import static org.smoothbuild.lang.base.type.TestingTypes.INFERABLE_BASE_TYPES;
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
import static org.smoothbuild.lang.base.type.TestingTypes.struct;
import static org.smoothbuild.util.Lists.concat;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Sets.set;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;

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
        arguments(f(BOOL, BLOB), "Bool(Blob)")
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

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<Type> types = ImmutableList.<Type>builder()
        .addAll(ELEMENTARY_TYPES)
        .add(A)
        .add(B)
        .add(struct("MyStruct", list()))
        .add(struct("MyStruct", list(itemSignature(BOOL, "field"))))
        .add(struct("MyStruct2", list(itemSignature(BOOL, "field"))))
        .add(struct("MyStruct", list(itemSignature(STRING, "field"))))
        .add(struct("MyStruct", list(itemSignature(BOOL, "field2"))))
        .build();
    for (Type type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(a(type), a(type));
      equalsTester.addEqualityGroup(a(a(type)), a(a(type)));
    }
    equalsTester.testEquals();
  }
}
