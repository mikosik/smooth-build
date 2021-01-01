package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.TestingItem.field;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.DATA;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_NON_POLYTYPE_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.ELEMENTARY_TYPES;
import static org.smoothbuild.lang.base.type.TestingTypes.FLAG;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.lang.base.type.constraint.TestingVariableToBounds.vtb;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.util.Lists;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class TypeTest {
  @Test
  public void verify_all_base_types_are_tested() {
    assertThat(BASE_TYPES)
        .hasSize(5);
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
        arguments(ANY, "Any"),
        arguments(BOOL, "Bool"),
        arguments(STRING, "String"),
        arguments(BLOB, "Blob"),
        arguments(NOTHING, "Nothing"),
        arguments(PERSON, "Person"),
        arguments(A, "A"),

        arguments(a(ANY), "[Any]"),
        arguments(a(BOOL), "[Bool]"),
        arguments(a(STRING), "[String]"),
        arguments(a(BLOB), "[Blob]"),
        arguments(a(NOTHING), "[Nothing]"),
        arguments(a(PERSON), "[Person]"),
        arguments(a(A), "[A]"),

        arguments(a(a(ANY)), "[[Any]]"),
        arguments(a(a(BOOL)), "[[Bool]]"),
        arguments(a(a(STRING)), "[[String]]"),
        arguments(a(a(BLOB)), "[[Blob]]"),
        arguments(a(a(NOTHING)), "[[Nothing]]"),
        arguments(a(a(PERSON)), "[[Person]]"),
        arguments(a(a(A)), "[[A]]")
    );
  }

  @ParameterizedTest
  @MethodSource("mapTypeVariable_test_data")
  public void mapTypeVariable(Type type, VariableToBounds variableToBounds, Type expected) {
    if (expected == null) {
      assertCall(() -> type.mapTypeVariables(variableToBounds, LOWER))
          .throwsException(new UnsupportedOperationException(
              arrayTypeVariable(type).toString() + " is not generic"));
    } else {
      assertThat(type.mapTypeVariables(variableToBounds, LOWER))
          .isEqualTo(expected);
    }
  }

  private static Type arrayTypeVariable(Type type) {
    if (type instanceof ArrayType arrayType) {
      return arrayTypeVariable(arrayType.elemType());
    } else {
      return type;
    }
  }

  public static List<Arguments> mapTypeVariable_test_data() {
    var result = new ArrayList<Arguments>();
    for (TypeVariable type : List.of(A, B)) {
      for (Type newCore : ELEMENTARY_TYPES) {
        Type typeArray = a(type);
        ArrayType newCoreArray = a(newCore);
        result.add(arguments(type, vtb(type, LOWER, newCore), newCore));
        result.add(arguments(typeArray, vtb(type, LOWER, newCore), newCoreArray));
        result.add(arguments(type, vtb(type, LOWER, newCoreArray), newCoreArray));
        result.add(arguments(typeArray, vtb(type, LOWER, newCoreArray), a(a(newCore))));
      }
    }
    for (Type type : ELEMENTARY_NON_POLYTYPE_TYPES) {
      Type typeArray = a(type);
      result.add(arguments(type, VariableToBounds.empty(), type));
      result.add(arguments(typeArray, VariableToBounds.empty(), typeArray));
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("isPolytype_test_data")
  public void isPolytype(Type type, boolean expected) {
    assertThat(type.isPolytype())
        .isEqualTo(expected);
  }

  public static List<Arguments> isPolytype_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : ELEMENTARY_NON_POLYTYPE_TYPES) {
      result.add(arguments(type, false));
      result.add(arguments(a(type), false));
      result.add(arguments(a(a(type)), false));
    }
    result.add(arguments(A, true));
    result.add(arguments(a(A), true));
    result.add(arguments(a(a(A)), true));

    return result;
  }

  @ParameterizedTest
  @MethodSource("isAssignableFrom_test_data")
  public void isAssignableFrom(TestedAssignmentSpec spec) {
    assertThat(spec.target.type().isAssignableFrom(spec.source.type()))
        .isEqualTo(spec.allowed);
  }

  public static List<TestedAssignmentSpec> isAssignableFrom_test_data() {
    return TestedAssignmentSpec.assignment_test_specs();
  }

  @ParameterizedTest
  @MethodSource("isParamAssignableFrom_test_data")
  public void isParamAssignableFrom(TestedAssignmentSpec spec) {
    assertThat(spec.target.type().isParamAssignableFrom(spec.source.type()))
        .isEqualTo(spec.allowed);
  }

  public static List<TestedAssignmentSpec> isParamAssignableFrom_test_data() {
    return TestedAssignmentSpec.parameter_assignment_test_specs();
  }

  @ParameterizedTest
  @MethodSource("inferVariableBounds_test_data")
  public void inferVariableBounds(Type type, Type assigned, VariableToBounds expected) {
    assertThat(type.inferVariableBounds(assigned, LOWER))
        .isEqualTo(expected);
  }

  public static List<Arguments> inferVariableBounds_test_data() {
    var result = new ArrayList<Arguments>();
    for (Type type : Lists.concat(ELEMENTARY_TYPES, B)) {
      if (type instanceof NothingType) {
        result.add(arguments(A, NOTHING, vtb(A, LOWER, NOTHING)));
        result.add(arguments(A, a(NOTHING), vtb(A, LOWER, a(NOTHING))));
        result.add(arguments(A, a(a(NOTHING)), vtb(A, LOWER, a(a(NOTHING)))));

        result.add(arguments(a(A), NOTHING, vtb(A, LOWER, NOTHING)));
        result.add(arguments(a(A), a(NOTHING), vtb(A, LOWER, NOTHING)));
        result.add(arguments(a(A), a(a(NOTHING)), vtb(A, LOWER, a(NOTHING))));

        result.add(arguments(a(a(A)), NOTHING, vtb(A, LOWER, NOTHING)));
        result.add(arguments(a(a(A)), a(NOTHING), vtb(A, LOWER, NOTHING)));
        result.add(arguments(a(a(A)), a(a(NOTHING)), vtb(A, LOWER, NOTHING)));
      } else {
        result.add(arguments(A, type, vtb(A, LOWER, type)));
        result.add(arguments(A, a(type), vtb(A, LOWER, a(type))));
        result.add(arguments(A, a(a(type)), vtb(A, LOWER, a(a(type)))));

        result.add(arguments(a(A), type, VariableToBounds.empty()));
        result.add(arguments(a(A), a(type), vtb(A, LOWER, type)));
        result.add(arguments(a(A), a(a(type)), vtb(A, LOWER, a(type))));

        result.add(arguments(a(a(A)), type, VariableToBounds.empty()));
        result.add(arguments(a(a(A)), a(type), VariableToBounds.empty()));
        result.add(arguments(a(a(A)), a(a(type)), vtb(A,
            LOWER, type)));
      }
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("mergeWith_upper_direction_test_data")
  public void mergeWith_upper_direction(Type type1, Type type2, Type expected) {
    assertThat(type1.mergeWith(type2, UPPER))
        .isEqualTo(expected);
    assertThat(type2.mergeWith(type1, UPPER))
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("mergeWith_lower_direction_test_data")
  public void mergeWith_lower_direction(Type type1, Type type2, Type expected) {
    assertThat(type1.mergeWith(type2, LOWER))
        .isEqualTo(expected);
    assertThat(type2.mergeWith(type1, LOWER))
        .isEqualTo(expected);
  }

  public static List<Arguments> mergeWith_upper_direction_test_data() {
    return List.of(
        arguments(ANY, ANY, ANY),
        arguments(ANY, BLOB, ANY),
        arguments(ANY, BOOL, ANY),
        arguments(ANY, NOTHING, ANY),
        arguments(ANY, STRING, ANY),
        arguments(ANY, DATA, ANY),
        arguments(ANY, FLAG, ANY),
        arguments(ANY, PERSON, ANY),
        arguments(ANY, A, ANY),
        arguments(ANY, a(ANY), ANY),
        arguments(ANY, a(BLOB), ANY),
        arguments(ANY, a(BOOL), ANY),
        arguments(ANY, a(NOTHING), ANY),
        arguments(ANY, a(STRING), ANY),
        arguments(ANY, a(DATA), ANY),
        arguments(ANY, a(FLAG), ANY),
        arguments(ANY, a(PERSON), ANY),
        arguments(ANY, a(A), ANY),

        arguments(BLOB, BLOB, BLOB),
        arguments(BLOB, BOOL, ANY),
        arguments(BLOB, NOTHING, BLOB),
        arguments(BLOB, STRING, ANY),
        arguments(BLOB, DATA, ANY),
        arguments(BLOB, FLAG, ANY),
        arguments(BLOB, PERSON, ANY),
        arguments(BLOB, A, ANY),
        arguments(BLOB, a(ANY), ANY),
        arguments(BLOB, a(BLOB), ANY),
        arguments(BLOB, a(BOOL), ANY),
        arguments(BLOB, a(NOTHING), ANY),
        arguments(BLOB, a(STRING), ANY),
        arguments(BLOB, a(DATA), ANY),
        arguments(BLOB, a(FLAG), ANY),
        arguments(BLOB, a(PERSON), ANY),
        arguments(BLOB, a(A), ANY),

        arguments(BOOL, BOOL, BOOL),
        arguments(BOOL, NOTHING, BOOL),
        arguments(BOOL, STRING, ANY),
        arguments(BOOL, DATA, ANY),
        arguments(BOOL, FLAG, ANY),
        arguments(BOOL, PERSON, ANY),
        arguments(BOOL, A, ANY),
        arguments(BOOL, a(ANY), ANY),
        arguments(BOOL, a(BLOB), ANY),
        arguments(BOOL, a(BOOL), ANY),
        arguments(BOOL, a(NOTHING), ANY),
        arguments(BOOL, a(STRING), ANY),
        arguments(BOOL, a(DATA), ANY),
        arguments(BOOL, a(FLAG), ANY),
        arguments(BOOL, a(PERSON), ANY),
        arguments(BOOL, a(A), ANY),

        arguments(NOTHING, STRING, STRING),
        arguments(NOTHING, DATA, DATA),
        arguments(NOTHING, FLAG, FLAG),
        arguments(NOTHING, PERSON, PERSON),
        arguments(NOTHING, A, A),
        arguments(NOTHING, a(ANY), a(ANY)),
        arguments(NOTHING, a(BLOB), a(BLOB)),
        arguments(NOTHING, a(BOOL), a(BOOL)),
        arguments(NOTHING, a(NOTHING), a(NOTHING)),
        arguments(NOTHING, a(STRING), a(STRING)),
        arguments(NOTHING, a(DATA), a(DATA)),
        arguments(NOTHING, a(FLAG), a(FLAG)),
        arguments(NOTHING, a(PERSON), a(PERSON)),
        arguments(NOTHING, a(A), a(A)),

        arguments(STRING, STRING, STRING),
        arguments(STRING, DATA, ANY),
        arguments(STRING, FLAG, ANY),
        arguments(STRING, PERSON, ANY),
        arguments(STRING, A, ANY),
        arguments(STRING, a(ANY), ANY),
        arguments(STRING, a(BLOB), ANY),
        arguments(STRING, a(BOOL), ANY),
        arguments(STRING, a(NOTHING), ANY),
        arguments(STRING, a(STRING), ANY),
        arguments(STRING, a(DATA), ANY),
        arguments(STRING, a(FLAG), ANY),
        arguments(STRING, a(PERSON), ANY),
        arguments(STRING, a(A), ANY),

        arguments(DATA, DATA, DATA),
        arguments(DATA, FLAG, ANY),
        arguments(DATA, PERSON, ANY),
        arguments(DATA, A, ANY),
        arguments(DATA, a(ANY), ANY),
        arguments(DATA, a(BLOB), ANY),
        arguments(DATA, a(BOOL), ANY),
        arguments(DATA, a(NOTHING), ANY),
        arguments(DATA, a(STRING), ANY),
        arguments(DATA, a(DATA), ANY),
        arguments(DATA, a(FLAG), ANY),
        arguments(DATA, a(PERSON), ANY),
        arguments(DATA, a(A), ANY),

        arguments(FLAG, FLAG, FLAG),
        arguments(FLAG, PERSON, ANY),
        arguments(FLAG, A, ANY),
        arguments(FLAG, a(ANY), ANY),
        arguments(FLAG, a(BLOB), ANY),
        arguments(FLAG, a(BOOL), ANY),
        arguments(FLAG, a(NOTHING), ANY),
        arguments(FLAG, a(STRING), ANY),
        arguments(FLAG, a(DATA), ANY),
        arguments(FLAG, a(FLAG), ANY),
        arguments(FLAG, a(PERSON), ANY),
        arguments(FLAG, a(A), ANY),

        arguments(PERSON, PERSON, PERSON),
        arguments(PERSON, A, ANY),
        arguments(PERSON, a(ANY), ANY),
        arguments(PERSON, a(BLOB), ANY),
        arguments(PERSON, a(BOOL), ANY),
        arguments(PERSON, a(NOTHING), ANY),
        arguments(PERSON, a(STRING), ANY),
        arguments(PERSON, a(DATA), ANY),
        arguments(PERSON, a(FLAG), ANY),
        arguments(PERSON, a(PERSON), ANY),
        arguments(PERSON, a(A), ANY),

        arguments(A, A, A),
        arguments(A, B, ANY),
        arguments(A, a(ANY), ANY),
        arguments(A, a(BLOB), ANY),
        arguments(A, a(BOOL), ANY),
        arguments(A, a(NOTHING), ANY),
        arguments(A, a(STRING), ANY),
        arguments(A, a(DATA), ANY),
        arguments(A, a(FLAG), ANY),
        arguments(A, a(PERSON), ANY),
        arguments(A, a(A), ANY),
        arguments(A, a(B), ANY),

        arguments(a(ANY), a(ANY), a(ANY)),
        arguments(a(ANY), a(BLOB), a(ANY)),
        arguments(a(ANY), a(BOOL), a(ANY)),
        arguments(a(ANY), a(NOTHING), a(ANY)),
        arguments(a(ANY), a(STRING), a(ANY)),
        arguments(a(ANY), a(DATA), a(ANY)),
        arguments(a(ANY), a(FLAG), a(ANY)),
        arguments(a(ANY), a(PERSON), a(ANY)),
        arguments(a(ANY), a(A), a(ANY)),

        arguments(a(BLOB), a(BLOB), a(BLOB)),
        arguments(a(BLOB), a(BOOL), a(ANY)),
        arguments(a(BLOB), a(NOTHING), a(BLOB)),
        arguments(a(BLOB), a(STRING), a(ANY)),
        arguments(a(BLOB), a(DATA), a(ANY)),
        arguments(a(BLOB), a(FLAG), a(ANY)),
        arguments(a(BLOB), a(PERSON), a(ANY)),
        arguments(a(BLOB), a(A), a(ANY)),

        arguments(a(BOOL), a(BOOL), a(BOOL)),
        arguments(a(BOOL), a(NOTHING), a(BOOL)),
        arguments(a(BOOL), a(STRING), a(ANY)),
        arguments(a(BOOL), a(DATA), a(ANY)),
        arguments(a(BOOL), a(FLAG), a(ANY)),
        arguments(a(BOOL), a(PERSON), a(ANY)),
        arguments(a(BOOL), a(A), a(ANY)),

        arguments(a(NOTHING), a(NOTHING), a(NOTHING)),
        arguments(a(NOTHING), a(STRING), a(STRING)),
        arguments(a(NOTHING), a(A), a(A)),
        arguments(a(NOTHING), a(DATA), a(DATA)),
        arguments(a(NOTHING), a(FLAG), a(FLAG)),
        arguments(a(NOTHING), a(PERSON), a(PERSON)),
        arguments(a(NOTHING), a(a(NOTHING)), a(a(NOTHING))),

        arguments(a(STRING), a(STRING), a(STRING)),
        arguments(a(STRING), a(A), a(ANY)),
        arguments(a(STRING), a(DATA), a(ANY)),
        arguments(a(STRING), a(FLAG), a(ANY)),
        arguments(a(STRING), a(PERSON), a(ANY)),

        arguments(a(A), a(A), a(A)),
        arguments(a(A), a(B), a(ANY)),
        arguments(a(A), a(DATA), a(ANY)),
        arguments(a(A), a(FLAG), a(ANY)),
        arguments(a(A), a(PERSON), a(ANY)),

        arguments(a(DATA), a(DATA), a(DATA)),
        arguments(a(DATA), a(FLAG), a(ANY)),
        arguments(a(DATA), a(PERSON), a(ANY)),

        arguments(a(FLAG), a(FLAG), a(FLAG)),
        arguments(a(FLAG), a(PERSON), a(ANY)),

        arguments(a(PERSON), a(PERSON), a(PERSON))
    );
  }

  public static List<Arguments> mergeWith_lower_direction_test_data() {
    return List.of(
        arguments(ANY, ANY, ANY),
        arguments(ANY, BLOB, BLOB),
        arguments(ANY, BOOL, BOOL),
        arguments(ANY, NOTHING, NOTHING),
        arguments(ANY, STRING, STRING),
        arguments(ANY, DATA, DATA),
        arguments(ANY, FLAG, FLAG),
        arguments(ANY, PERSON, PERSON),
        arguments(ANY, A, A),
        arguments(ANY, a(ANY), a(ANY)),
        arguments(ANY, a(BLOB), a(BLOB)),
        arguments(ANY, a(BOOL), a(BOOL)),
        arguments(ANY, a(NOTHING), a(NOTHING)),
        arguments(ANY, a(STRING), a(STRING)),
        arguments(ANY, a(DATA), a(DATA)),
        arguments(ANY, a(FLAG), a(FLAG)),
        arguments(ANY, a(PERSON), a(PERSON)),
        arguments(ANY, a(A), a(A)),

        arguments(BOOL, BOOL, BOOL),
        arguments(BOOL, NOTHING, NOTHING),
        arguments(BOOL, STRING, NOTHING),
        arguments(BOOL, DATA, NOTHING),
        arguments(BOOL, FLAG, NOTHING),
        arguments(BOOL, PERSON, NOTHING),
        arguments(BOOL, A, NOTHING),
        arguments(BOOL, a(ANY), NOTHING),
        arguments(BOOL, a(BLOB), NOTHING),
        arguments(BOOL, a(BOOL), NOTHING),
        arguments(BOOL, a(NOTHING), NOTHING),
        arguments(BOOL, a(STRING), NOTHING),
        arguments(BOOL, a(DATA), NOTHING),
        arguments(BOOL, a(FLAG), NOTHING),
        arguments(BOOL, a(PERSON), NOTHING),
        arguments(BOOL, a(A), NOTHING),

        arguments(NOTHING, STRING, NOTHING),
        arguments(NOTHING, DATA, NOTHING),
        arguments(NOTHING, FLAG, NOTHING),
        arguments(NOTHING, PERSON, NOTHING),
        arguments(NOTHING, A, NOTHING),
        arguments(NOTHING, a(ANY), NOTHING),
        arguments(NOTHING, a(BLOB), NOTHING),
        arguments(NOTHING, a(BOOL), NOTHING),
        arguments(NOTHING, a(NOTHING), NOTHING),
        arguments(NOTHING, a(STRING), NOTHING),
        arguments(NOTHING, a(DATA), NOTHING),
        arguments(NOTHING, a(FLAG), NOTHING),
        arguments(NOTHING, a(PERSON), NOTHING),
        arguments(NOTHING, a(A), NOTHING),

        arguments(STRING, STRING, STRING),
        arguments(STRING, DATA, NOTHING),
        arguments(STRING, FLAG, NOTHING),
        arguments(STRING, PERSON, NOTHING),
        arguments(STRING, A, NOTHING),
        arguments(STRING, a(ANY), NOTHING),
        arguments(STRING, a(BLOB), NOTHING),
        arguments(STRING, a(BOOL), NOTHING),
        arguments(STRING, a(NOTHING), NOTHING),
        arguments(STRING, a(STRING), NOTHING),
        arguments(STRING, a(DATA), NOTHING),
        arguments(STRING, a(FLAG), NOTHING),
        arguments(STRING, a(PERSON), NOTHING),
        arguments(STRING, a(A), NOTHING),

        arguments(DATA, DATA, DATA),
        arguments(DATA, FLAG, NOTHING),
        arguments(DATA, PERSON, NOTHING),
        arguments(DATA, A, NOTHING),
        arguments(DATA, a(ANY), NOTHING),
        arguments(DATA, a(BLOB), NOTHING),
        arguments(DATA, a(BOOL), NOTHING),
        arguments(DATA, a(NOTHING), NOTHING),
        arguments(DATA, a(STRING), NOTHING),
        arguments(DATA, a(DATA), NOTHING),
        arguments(DATA, a(FLAG), NOTHING),
        arguments(DATA, a(PERSON), NOTHING),
        arguments(DATA, a(A), NOTHING),

        arguments(FLAG, FLAG, FLAG),
        arguments(FLAG, PERSON, NOTHING),
        arguments(FLAG, A, NOTHING),
        arguments(FLAG, a(ANY), NOTHING),
        arguments(FLAG, a(BLOB), NOTHING),
        arguments(FLAG, a(BOOL), NOTHING),
        arguments(FLAG, a(NOTHING), NOTHING),
        arguments(FLAG, a(STRING), NOTHING),
        arguments(FLAG, a(DATA), NOTHING),
        arguments(FLAG, a(FLAG), NOTHING),
        arguments(FLAG, a(PERSON), NOTHING),
        arguments(FLAG, a(A), NOTHING),

        arguments(PERSON, PERSON, PERSON),
        arguments(PERSON, A, NOTHING),
        arguments(PERSON, a(ANY), NOTHING),
        arguments(PERSON, a(BLOB), NOTHING),
        arguments(PERSON, a(BOOL), NOTHING),
        arguments(PERSON, a(NOTHING), NOTHING),
        arguments(PERSON, a(STRING), NOTHING),
        arguments(PERSON, a(DATA), NOTHING),
        arguments(PERSON, a(FLAG), NOTHING),
        arguments(PERSON, a(PERSON), NOTHING),
        arguments(PERSON, a(A), NOTHING),

        arguments(A, A, A),
        arguments(A, B, NOTHING),
        arguments(A, a(ANY), NOTHING),
        arguments(A, a(BLOB), NOTHING),
        arguments(A, a(BOOL), NOTHING),
        arguments(A, a(NOTHING), NOTHING),
        arguments(A, a(STRING), NOTHING),
        arguments(A, a(DATA), NOTHING),
        arguments(A, a(FLAG), NOTHING),
        arguments(A, a(PERSON), NOTHING),
        arguments(A, a(A), NOTHING),
        arguments(A, a(B), NOTHING),

        arguments(a(ANY), a(ANY), a(ANY)),
        arguments(a(ANY), a(BLOB), a(BLOB)),
        arguments(a(ANY), a(BOOL), a(BOOL)),
        arguments(a(ANY), a(NOTHING), a(NOTHING)),
        arguments(a(ANY), a(STRING), a(STRING)),
        arguments(a(ANY), a(DATA), a(DATA)),
        arguments(a(ANY), a(FLAG), a(FLAG)),
        arguments(a(ANY), a(PERSON), a(PERSON)),
        arguments(a(ANY), a(A), a(A)),

        arguments(a(BLOB), a(BLOB), a(BLOB)),
        arguments(a(BLOB), a(BOOL), a(NOTHING)),
        arguments(a(BLOB), a(NOTHING), a(NOTHING)),
        arguments(a(BLOB), a(STRING), a(NOTHING)),
        arguments(a(BLOB), a(DATA), a(NOTHING)),
        arguments(a(BLOB), a(FLAG), a(NOTHING)),
        arguments(a(BLOB), a(PERSON), a(NOTHING)),
        arguments(a(BLOB), a(A), a(NOTHING)),

        arguments(a(BOOL), a(BOOL), a(BOOL)),
        arguments(a(BOOL), a(NOTHING), a(NOTHING)),
        arguments(a(BOOL), a(STRING), a(NOTHING)),
        arguments(a(BOOL), a(DATA), a(NOTHING)),
        arguments(a(BOOL), a(FLAG), a(NOTHING)),
        arguments(a(BOOL), a(PERSON), a(NOTHING)),
        arguments(a(BOOL), a(A), a(NOTHING)),

        arguments(a(NOTHING), a(NOTHING), a(NOTHING)),
        arguments(a(NOTHING), a(STRING), a(NOTHING)),
        arguments(a(NOTHING), a(A), a(NOTHING)),
        arguments(a(NOTHING), a(DATA), a(NOTHING)),
        arguments(a(NOTHING), a(FLAG), a(NOTHING)),
        arguments(a(NOTHING), a(PERSON), a(NOTHING)),
        arguments(a(NOTHING), a(a(NOTHING)), a(NOTHING)),

        arguments(a(STRING), a(STRING), a(STRING)),
        arguments(a(STRING), a(A), a(NOTHING)),
        arguments(a(STRING), a(DATA), a(NOTHING)),
        arguments(a(STRING), a(FLAG), a(NOTHING)),
        arguments(a(STRING), a(PERSON), a(NOTHING)),

        arguments(a(A), a(A), a(A)),
        arguments(a(A), a(B), a(NOTHING)),
        arguments(a(A), a(DATA), a(NOTHING)),
        arguments(a(A), a(FLAG), a(NOTHING)),
        arguments(a(A), a(PERSON), a(NOTHING)),

        arguments(a(DATA), a(DATA), a(DATA)),
        arguments(a(DATA), a(FLAG), a(NOTHING)),
        arguments(a(DATA), a(PERSON), a(NOTHING)),

        arguments(a(FLAG), a(FLAG), a(FLAG)),
        arguments(a(FLAG), a(PERSON), a(NOTHING)),

        arguments(a(PERSON), a(PERSON), a(PERSON))
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
    for (Type type : ELEMENTARY_TYPES) {
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
        .add(B)
        .add(struct("MyStruct", list()))
        .add(struct("MyStruct", list(field("field"))))
        .add(struct("MyStruct2", list(field("field"))))
        .add(struct("MyStruct", list(field("field2"))))
        .build();
    for (Type type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(a(type), a(type));
      equalsTester.addEqualityGroup(a(a(type)), a(a(type)));
    }
    equalsTester.testEquals();
  }
}
