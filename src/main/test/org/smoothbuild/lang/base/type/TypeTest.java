package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_DATA;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_FLAG;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_STRING;
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
import static org.smoothbuild.lang.base.type.Types.BASE_TYPES;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.lang.base.type.constraint.TestingConstraints.constraints;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.common.TestingLocation.loc;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.TestingItem;
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

        arguments(ARRAY_ANY, "[Any]"),
        arguments(ARRAY_BOOL, "[Bool]"),
        arguments(ARRAY_STRING, "[String]"),
        arguments(ARRAY_BLOB, "[Blob]"),
        arguments(ARRAY_NOTHING, "[Nothing]"),
        arguments(ARRAY_PERSON, "[Person]"),
        arguments(ARRAY_A, "[A]"),

        arguments(ARRAY2_ANY, "[[Any]]"),
        arguments(ARRAY2_BOOL, "[[Bool]]"),
        arguments(ARRAY2_STRING, "[[String]]"),
        arguments(ARRAY2_BLOB, "[[Blob]]"),
        arguments(ARRAY2_NOTHING, "[[Nothing]]"),
        arguments(ARRAY2_PERSON, "[[Person]]"),
        arguments(ARRAY2_A, "[[A]]")
    );
  }

  @ParameterizedTest
  @MethodSource("mapTypeVariable_test_data")
  public void mapTypeVariable(Type type, VariableToBounds variableToBounds, Type expected) {
    if (expected == null) {
      assertCall(() -> type.mapTypeVariables(variableToBounds))
          .throwsException(new UnsupportedOperationException(
              arrayTypeVariable(type).toString() + " is not generic"));
    } else {
      assertThat(type.mapTypeVariables(variableToBounds))
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
        Type typeArray = array(type);
        ArrayType newCoreArray = array(newCore);
        result.add(arguments(type, constraints(type, newCore), newCore));
        result.add(arguments(typeArray, constraints(type, newCore), newCoreArray));
        result.add(arguments(type, constraints(type, newCoreArray), newCoreArray));
        result.add(arguments(typeArray, constraints(type, newCoreArray), array(array(newCore))));
      }
    }
    for (Type type : ELEMENTARY_NON_POLYTYPE_TYPES) {
      Type typeArray = array(type);
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
      result.add(arguments(array(type), false));
      result.add(arguments(array(array(type)), false));
    }
    result.add(arguments(A, true));
    result.add(arguments(array(A), true));
    result.add(arguments(array(array(A)), true));

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
        result.add(arguments(A, NOTHING, constraints(A, NOTHING)));
        result.add(arguments(A, array(NOTHING), constraints(A, array(NOTHING))));
        result.add(arguments(A, array(array(NOTHING)), constraints(A, array(array(NOTHING)))));

        result.add(arguments(array(A), NOTHING, constraints(A, NOTHING)));
        result.add(arguments(array(A), array(NOTHING), constraints(A, NOTHING)));
        result.add(arguments(array(A), array(array(NOTHING)), constraints(A, array(NOTHING))));

        result.add(arguments(array(array(A)), NOTHING, constraints(A, NOTHING)));
        result.add(arguments(array(array(A)), array(NOTHING), constraints(A, NOTHING)));
        result.add(arguments(array(array(A)), array(array(NOTHING)), constraints(A, NOTHING)));
      } else {
        result.add(arguments(A, type, constraints(A, type)));
        result.add(arguments(A, array(type), constraints(A, array(type))));
        result.add(arguments(A, array(array(type)), constraints(A, array(array(type)))));

        result.add(arguments(array(A), type, VariableToBounds.empty()));
        result.add(arguments(array(A), array(type), constraints(A, type)));
        result.add(arguments(array(A), array(array(type)), constraints(A, array(type))));

        result.add(arguments(array(array(A)), type, VariableToBounds.empty()));
        result.add(arguments(array(array(A)), array(type), VariableToBounds.empty()));
        result.add(arguments(array(array(A)), array(array(type)), constraints(A,
            type)));
      }
    }
    return result;
  }

  @ParameterizedTest
  @MethodSource("joinWith_test_data")
  public void mergeWith_upper_direction(Type type1, Type type2, Type expected) {
    assertThat(type1.mergeWith(type2, UPPER))
        .isEqualTo(expected);
    assertThat(type2.mergeWith(type1, UPPER))
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("meetWith_test_data")
  public void mergeWith_lower_direction(Type type1, Type type2, Type expected) {
    assertThat(type1.mergeWith(type2, LOWER))
        .isEqualTo(expected);
    assertThat(type2.mergeWith(type1, LOWER))
        .isEqualTo(expected);
  }

  @ParameterizedTest
  @MethodSource("joinWith_test_data")
  public void joinWith(Type type1, Type type2, Type expected) {
    assertThat(type1.joinWith(type2))
        .isEqualTo(expected);
    assertThat(type2.joinWith(type1))
        .isEqualTo(expected);
  }

  public static List<Arguments> joinWith_test_data() {
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
        arguments(ANY, ARRAY_ANY, ANY),
        arguments(ANY, ARRAY_BLOB, ANY),
        arguments(ANY, ARRAY_BOOL, ANY),
        arguments(ANY, ARRAY_NOTHING, ANY),
        arguments(ANY, ARRAY_STRING, ANY),
        arguments(ANY, ARRAY_DATA, ANY),
        arguments(ANY, ARRAY_FLAG, ANY),
        arguments(ANY, ARRAY_PERSON, ANY),
        arguments(ANY, ARRAY_A, ANY),

        arguments(BLOB, BLOB, BLOB),
        arguments(BLOB, BOOL, ANY),
        arguments(BLOB, NOTHING, BLOB),
        arguments(BLOB, STRING, ANY),
        arguments(BLOB, DATA, ANY),
        arguments(BLOB, FLAG, ANY),
        arguments(BLOB, PERSON, ANY),
        arguments(BLOB, A, ANY),
        arguments(BLOB, ARRAY_ANY, ANY),
        arguments(BLOB, ARRAY_BLOB, ANY),
        arguments(BLOB, ARRAY_BOOL, ANY),
        arguments(BLOB, ARRAY_NOTHING, ANY),
        arguments(BLOB, ARRAY_STRING, ANY),
        arguments(BLOB, ARRAY_DATA, ANY),
        arguments(BLOB, ARRAY_FLAG, ANY),
        arguments(BLOB, ARRAY_PERSON, ANY),
        arguments(BLOB, ARRAY_A, ANY),

        arguments(BOOL, BOOL, BOOL),
        arguments(BOOL, NOTHING, BOOL),
        arguments(BOOL, STRING, ANY),
        arguments(BOOL, DATA, ANY),
        arguments(BOOL, FLAG, ANY),
        arguments(BOOL, PERSON, ANY),
        arguments(BOOL, A, ANY),
        arguments(BOOL, ARRAY_ANY, ANY),
        arguments(BOOL, ARRAY_BLOB, ANY),
        arguments(BOOL, ARRAY_BOOL, ANY),
        arguments(BOOL, ARRAY_NOTHING, ANY),
        arguments(BOOL, ARRAY_STRING, ANY),
        arguments(BOOL, ARRAY_DATA, ANY),
        arguments(BOOL, ARRAY_FLAG, ANY),
        arguments(BOOL, ARRAY_PERSON, ANY),
        arguments(BOOL, ARRAY_A, ANY),

        arguments(NOTHING, STRING, STRING),
        arguments(NOTHING, DATA, DATA),
        arguments(NOTHING, FLAG, FLAG),
        arguments(NOTHING, PERSON, PERSON),
        arguments(NOTHING, A, A),
        arguments(NOTHING, ARRAY_ANY, ARRAY_ANY),
        arguments(NOTHING, ARRAY_BLOB, ARRAY_BLOB),
        arguments(NOTHING, ARRAY_BOOL, ARRAY_BOOL),
        arguments(NOTHING, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(NOTHING, ARRAY_STRING, ARRAY_STRING),
        arguments(NOTHING, ARRAY_DATA, ARRAY_DATA),
        arguments(NOTHING, ARRAY_FLAG, ARRAY_FLAG),
        arguments(NOTHING, ARRAY_PERSON, ARRAY_PERSON),
        arguments(NOTHING, ARRAY_A, ARRAY_A),

        arguments(STRING, STRING, STRING),
        arguments(STRING, DATA, ANY),
        arguments(STRING, FLAG, ANY),
        arguments(STRING, PERSON, ANY),
        arguments(STRING, A, ANY),
        arguments(STRING, ARRAY_ANY, ANY),
        arguments(STRING, ARRAY_BLOB, ANY),
        arguments(STRING, ARRAY_BOOL, ANY),
        arguments(STRING, ARRAY_NOTHING, ANY),
        arguments(STRING, ARRAY_STRING, ANY),
        arguments(STRING, ARRAY_DATA, ANY),
        arguments(STRING, ARRAY_FLAG, ANY),
        arguments(STRING, ARRAY_PERSON, ANY),
        arguments(STRING, ARRAY_A, ANY),

        arguments(DATA, DATA, DATA),
        arguments(DATA, FLAG, ANY),
        arguments(DATA, PERSON, ANY),
        arguments(DATA, A, ANY),
        arguments(DATA, ARRAY_ANY, ANY),
        arguments(DATA, ARRAY_BLOB, ANY),
        arguments(DATA, ARRAY_BOOL, ANY),
        arguments(DATA, ARRAY_NOTHING, ANY),
        arguments(DATA, ARRAY_STRING, ANY),
        arguments(DATA, ARRAY_DATA, ANY),
        arguments(DATA, ARRAY_FLAG, ANY),
        arguments(DATA, ARRAY_PERSON, ANY),
        arguments(DATA, ARRAY_A, ANY),

        arguments(FLAG, FLAG, FLAG),
        arguments(FLAG, PERSON, ANY),
        arguments(FLAG, A, ANY),
        arguments(FLAG, ARRAY_ANY, ANY),
        arguments(FLAG, ARRAY_BLOB, ANY),
        arguments(FLAG, ARRAY_BOOL, ANY),
        arguments(FLAG, ARRAY_NOTHING, ANY),
        arguments(FLAG, ARRAY_STRING, ANY),
        arguments(FLAG, ARRAY_DATA, ANY),
        arguments(FLAG, ARRAY_FLAG, ANY),
        arguments(FLAG, ARRAY_PERSON, ANY),
        arguments(FLAG, ARRAY_A, ANY),

        arguments(PERSON, PERSON, PERSON),
        arguments(PERSON, A, ANY),
        arguments(PERSON, ARRAY_ANY, ANY),
        arguments(PERSON, ARRAY_BLOB, ANY),
        arguments(PERSON, ARRAY_BOOL, ANY),
        arguments(PERSON, ARRAY_NOTHING, ANY),
        arguments(PERSON, ARRAY_STRING, ANY),
        arguments(PERSON, ARRAY_DATA, ANY),
        arguments(PERSON, ARRAY_FLAG, ANY),
        arguments(PERSON, ARRAY_PERSON, ANY),
        arguments(PERSON, ARRAY_A, ANY),

        arguments(A, A, A),
        arguments(A, B, ANY),
        arguments(A, ARRAY_ANY, ANY),
        arguments(A, ARRAY_BLOB, ANY),
        arguments(A, ARRAY_BOOL, ANY),
        arguments(A, ARRAY_NOTHING, ANY),
        arguments(A, ARRAY_STRING, ANY),
        arguments(A, ARRAY_DATA, ANY),
        arguments(A, ARRAY_FLAG, ANY),
        arguments(A, ARRAY_PERSON, ANY),
        arguments(A, ARRAY_A, ANY),
        arguments(A, ARRAY_B, ANY),

        arguments(ARRAY_ANY, ARRAY_ANY, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_BLOB, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_BOOL, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_NOTHING, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_STRING, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_DATA, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_FLAG, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_PERSON, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_A, ARRAY_ANY),

        arguments(ARRAY_BLOB, ARRAY_BLOB, ARRAY_BLOB),
        arguments(ARRAY_BLOB, ARRAY_BOOL, ARRAY_ANY),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, ARRAY_BLOB),
        arguments(ARRAY_BLOB, ARRAY_STRING, ARRAY_ANY),
        arguments(ARRAY_BLOB, ARRAY_DATA, ARRAY_ANY),
        arguments(ARRAY_BLOB, ARRAY_FLAG, ARRAY_ANY),
        arguments(ARRAY_BLOB, ARRAY_PERSON, ARRAY_ANY),
        arguments(ARRAY_BLOB, ARRAY_A, ARRAY_ANY),

        arguments(ARRAY_BOOL, ARRAY_BOOL, ARRAY_BOOL),
        arguments(ARRAY_BOOL, ARRAY_NOTHING, ARRAY_BOOL),
        arguments(ARRAY_BOOL, ARRAY_STRING, ARRAY_ANY),
        arguments(ARRAY_BOOL, ARRAY_DATA, ARRAY_ANY),
        arguments(ARRAY_BOOL, ARRAY_FLAG, ARRAY_ANY),
        arguments(ARRAY_BOOL, ARRAY_PERSON, ARRAY_ANY),
        arguments(ARRAY_BOOL, ARRAY_A, ARRAY_ANY),

        arguments(ARRAY_NOTHING, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY_STRING, ARRAY_STRING),
        arguments(ARRAY_NOTHING, ARRAY_A, ARRAY_A),
        arguments(ARRAY_NOTHING, ARRAY_DATA, ARRAY_DATA),
        arguments(ARRAY_NOTHING, ARRAY_FLAG, ARRAY_FLAG),
        arguments(ARRAY_NOTHING, ARRAY_PERSON, ARRAY_PERSON),
        arguments(ARRAY_NOTHING, ARRAY2_NOTHING, ARRAY2_NOTHING),

        arguments(ARRAY_STRING, ARRAY_STRING, ARRAY_STRING),
        arguments(ARRAY_STRING, ARRAY_A, ARRAY_ANY),
        arguments(ARRAY_STRING, ARRAY_DATA, ARRAY_ANY),
        arguments(ARRAY_STRING, ARRAY_FLAG, ARRAY_ANY),
        arguments(ARRAY_STRING, ARRAY_PERSON, ARRAY_ANY),

        arguments(ARRAY_A, ARRAY_A, ARRAY_A),
        arguments(ARRAY_A, ARRAY_B, ARRAY_ANY),
        arguments(ARRAY_A, ARRAY_DATA, ARRAY_ANY),
        arguments(ARRAY_A, ARRAY_FLAG, ARRAY_ANY),
        arguments(ARRAY_A, ARRAY_PERSON, ARRAY_ANY),

        arguments(ARRAY_DATA, ARRAY_DATA, ARRAY_DATA),
        arguments(ARRAY_DATA, ARRAY_FLAG, ARRAY_ANY),
        arguments(ARRAY_DATA, ARRAY_PERSON, ARRAY_ANY),

        arguments(ARRAY_FLAG, ARRAY_FLAG, ARRAY_FLAG),
        arguments(ARRAY_FLAG, ARRAY_PERSON, ARRAY_ANY),

        arguments(ARRAY_PERSON, ARRAY_PERSON, ARRAY_PERSON)
    );
  }

  @ParameterizedTest
  @MethodSource("meetWith_test_data")
  public void meetWith(Type type1, Type type2, Type expected) {
    assertThat(type1.meetWith(type2))
        .isEqualTo(expected);
    assertThat(type2.meetWith(type1))
        .isEqualTo(expected);
  }

  public static List<Arguments> meetWith_test_data() {
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
        arguments(ANY, ARRAY_ANY, ARRAY_ANY),
        arguments(ANY, ARRAY_BLOB, ARRAY_BLOB),
        arguments(ANY, ARRAY_BOOL, ARRAY_BOOL),
        arguments(ANY, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(ANY, ARRAY_STRING, ARRAY_STRING),
        arguments(ANY, ARRAY_DATA, ARRAY_DATA),
        arguments(ANY, ARRAY_FLAG, ARRAY_FLAG),
        arguments(ANY, ARRAY_PERSON, ARRAY_PERSON),
        arguments(ANY, ARRAY_A, ARRAY_A),

        arguments(BOOL, BOOL, BOOL),
        arguments(BOOL, NOTHING, NOTHING),
        arguments(BOOL, STRING, NOTHING),
        arguments(BOOL, DATA, NOTHING),
        arguments(BOOL, FLAG, NOTHING),
        arguments(BOOL, PERSON, NOTHING),
        arguments(BOOL, A, NOTHING),
        arguments(BOOL, ARRAY_ANY, NOTHING),
        arguments(BOOL, ARRAY_BLOB, NOTHING),
        arguments(BOOL, ARRAY_BOOL, NOTHING),
        arguments(BOOL, ARRAY_NOTHING, NOTHING),
        arguments(BOOL, ARRAY_STRING, NOTHING),
        arguments(BOOL, ARRAY_DATA, NOTHING),
        arguments(BOOL, ARRAY_FLAG, NOTHING),
        arguments(BOOL, ARRAY_PERSON, NOTHING),
        arguments(BOOL, ARRAY_A, NOTHING),

        arguments(NOTHING, STRING, NOTHING),
        arguments(NOTHING, DATA, NOTHING),
        arguments(NOTHING, FLAG, NOTHING),
        arguments(NOTHING, PERSON, NOTHING),
        arguments(NOTHING, A, NOTHING),
        arguments(NOTHING, ARRAY_ANY, NOTHING),
        arguments(NOTHING, ARRAY_BLOB, NOTHING),
        arguments(NOTHING, ARRAY_BOOL, NOTHING),
        arguments(NOTHING, ARRAY_NOTHING, NOTHING),
        arguments(NOTHING, ARRAY_STRING, NOTHING),
        arguments(NOTHING, ARRAY_DATA, NOTHING),
        arguments(NOTHING, ARRAY_FLAG, NOTHING),
        arguments(NOTHING, ARRAY_PERSON, NOTHING),
        arguments(NOTHING, ARRAY_A, NOTHING),

        arguments(STRING, STRING, STRING),
        arguments(STRING, DATA, NOTHING),
        arguments(STRING, FLAG, NOTHING),
        arguments(STRING, PERSON, NOTHING),
        arguments(STRING, A, NOTHING),
        arguments(STRING, ARRAY_ANY, NOTHING),
        arguments(STRING, ARRAY_BLOB, NOTHING),
        arguments(STRING, ARRAY_BOOL, NOTHING),
        arguments(STRING, ARRAY_NOTHING, NOTHING),
        arguments(STRING, ARRAY_STRING, NOTHING),
        arguments(STRING, ARRAY_DATA, NOTHING),
        arguments(STRING, ARRAY_FLAG, NOTHING),
        arguments(STRING, ARRAY_PERSON, NOTHING),
        arguments(STRING, ARRAY_A, NOTHING),

        arguments(DATA, DATA, DATA),
        arguments(DATA, FLAG, NOTHING),
        arguments(DATA, PERSON, NOTHING),
        arguments(DATA, A, NOTHING),
        arguments(DATA, ARRAY_ANY, NOTHING),
        arguments(DATA, ARRAY_BLOB, NOTHING),
        arguments(DATA, ARRAY_BOOL, NOTHING),
        arguments(DATA, ARRAY_NOTHING, NOTHING),
        arguments(DATA, ARRAY_STRING, NOTHING),
        arguments(DATA, ARRAY_DATA, NOTHING),
        arguments(DATA, ARRAY_FLAG, NOTHING),
        arguments(DATA, ARRAY_PERSON, NOTHING),
        arguments(DATA, ARRAY_A, NOTHING),

        arguments(FLAG, FLAG, FLAG),
        arguments(FLAG, PERSON, NOTHING),
        arguments(FLAG, A, NOTHING),
        arguments(FLAG, ARRAY_ANY, NOTHING),
        arguments(FLAG, ARRAY_BLOB, NOTHING),
        arguments(FLAG, ARRAY_BOOL, NOTHING),
        arguments(FLAG, ARRAY_NOTHING, NOTHING),
        arguments(FLAG, ARRAY_STRING, NOTHING),
        arguments(FLAG, ARRAY_DATA, NOTHING),
        arguments(FLAG, ARRAY_FLAG, NOTHING),
        arguments(FLAG, ARRAY_PERSON, NOTHING),
        arguments(FLAG, ARRAY_A, NOTHING),

        arguments(PERSON, PERSON, PERSON),
        arguments(PERSON, A, NOTHING),
        arguments(PERSON, ARRAY_ANY, NOTHING),
        arguments(PERSON, ARRAY_BLOB, NOTHING),
        arguments(PERSON, ARRAY_BOOL, NOTHING),
        arguments(PERSON, ARRAY_NOTHING, NOTHING),
        arguments(PERSON, ARRAY_STRING, NOTHING),
        arguments(PERSON, ARRAY_DATA, NOTHING),
        arguments(PERSON, ARRAY_FLAG, NOTHING),
        arguments(PERSON, ARRAY_PERSON, NOTHING),
        arguments(PERSON, ARRAY_A, NOTHING),

        arguments(A, A, A),
        arguments(A, B, NOTHING),
        arguments(A, ARRAY_ANY, NOTHING),
        arguments(A, ARRAY_BLOB, NOTHING),
        arguments(A, ARRAY_BOOL, NOTHING),
        arguments(A, ARRAY_NOTHING, NOTHING),
        arguments(A, ARRAY_STRING, NOTHING),
        arguments(A, ARRAY_DATA, NOTHING),
        arguments(A, ARRAY_FLAG, NOTHING),
        arguments(A, ARRAY_PERSON, NOTHING),
        arguments(A, ARRAY_A, NOTHING),
        arguments(A, ARRAY_B, NOTHING),

        arguments(ARRAY_ANY, ARRAY_ANY, ARRAY_ANY),
        arguments(ARRAY_ANY, ARRAY_BLOB, ARRAY_BLOB),
        arguments(ARRAY_ANY, ARRAY_BOOL, ARRAY_BOOL),
        arguments(ARRAY_ANY, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_ANY, ARRAY_STRING, ARRAY_STRING),
        arguments(ARRAY_ANY, ARRAY_DATA, ARRAY_DATA),
        arguments(ARRAY_ANY, ARRAY_FLAG, ARRAY_FLAG),
        arguments(ARRAY_ANY, ARRAY_PERSON, ARRAY_PERSON),
        arguments(ARRAY_ANY, ARRAY_A, ARRAY_A),

        arguments(ARRAY_BLOB, ARRAY_BLOB, ARRAY_BLOB),
        arguments(ARRAY_BLOB, ARRAY_BOOL, ARRAY_NOTHING),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_BLOB, ARRAY_STRING, ARRAY_NOTHING),
        arguments(ARRAY_BLOB, ARRAY_DATA, ARRAY_NOTHING),
        arguments(ARRAY_BLOB, ARRAY_FLAG, ARRAY_NOTHING),
        arguments(ARRAY_BLOB, ARRAY_PERSON, ARRAY_NOTHING),
        arguments(ARRAY_BLOB, ARRAY_A, ARRAY_NOTHING),

        arguments(ARRAY_BOOL, ARRAY_BOOL, ARRAY_BOOL),
        arguments(ARRAY_BOOL, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_BOOL, ARRAY_STRING, ARRAY_NOTHING),
        arguments(ARRAY_BOOL, ARRAY_DATA, ARRAY_NOTHING),
        arguments(ARRAY_BOOL, ARRAY_FLAG, ARRAY_NOTHING),
        arguments(ARRAY_BOOL, ARRAY_PERSON, ARRAY_NOTHING),
        arguments(ARRAY_BOOL, ARRAY_A, ARRAY_NOTHING),

        arguments(ARRAY_NOTHING, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY_STRING, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY_A, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY_DATA, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY_FLAG, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY_PERSON, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY2_NOTHING, ARRAY_NOTHING),

        arguments(ARRAY_STRING, ARRAY_STRING, ARRAY_STRING),
        arguments(ARRAY_STRING, ARRAY_A, ARRAY_NOTHING),
        arguments(ARRAY_STRING, ARRAY_DATA, ARRAY_NOTHING),
        arguments(ARRAY_STRING, ARRAY_FLAG, ARRAY_NOTHING),
        arguments(ARRAY_STRING, ARRAY_PERSON, ARRAY_NOTHING),

        arguments(ARRAY_A, ARRAY_A, ARRAY_A),
        arguments(ARRAY_A, ARRAY_B, ARRAY_NOTHING),
        arguments(ARRAY_A, ARRAY_DATA, ARRAY_NOTHING),
        arguments(ARRAY_A, ARRAY_FLAG, ARRAY_NOTHING),
        arguments(ARRAY_A, ARRAY_PERSON, ARRAY_NOTHING),

        arguments(ARRAY_DATA, ARRAY_DATA, ARRAY_DATA),
        arguments(ARRAY_DATA, ARRAY_FLAG, ARRAY_NOTHING),
        arguments(ARRAY_DATA, ARRAY_PERSON, ARRAY_NOTHING),

        arguments(ARRAY_FLAG, ARRAY_FLAG, ARRAY_FLAG),
        arguments(ARRAY_FLAG, ARRAY_PERSON, ARRAY_NOTHING),

        arguments(ARRAY_PERSON, ARRAY_PERSON, ARRAY_PERSON)
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
      result.add(arguments(array(type), type));
      result.add(arguments(array(array(type)), array(type)));
      result.add(arguments(array(array(array(type))), array(array(type))));
    }
    return result;
  }

  @Test
  public void equality() {
    EqualsTester equalsTester = new EqualsTester();
    List<Type> types = ImmutableList.<Type>builder()
        .addAll(ELEMENTARY_TYPES)
        .add(B)
        .add(struct("MyStruct", loc(), list()))
        .add(struct("MyStruct", loc(), list(TestingItem.field("field"))))
        .add(struct("MyStruct2", loc(), list(TestingItem.field("field"))))
        .add(struct("MyStruct", loc(), list(TestingItem.field("field2"))))
        .build();
    for (Type type : types) {
      equalsTester.addEqualityGroup(type, type);
      equalsTester.addEqualityGroup(array(type), array(type));
      equalsTester.addEqualityGroup(array(array(type)), array(array(type)));
    }
    equalsTester.testEquals();
  }
}
