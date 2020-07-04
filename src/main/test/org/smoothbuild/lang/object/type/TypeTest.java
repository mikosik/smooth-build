package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.object.type.TestingTypes.a;
import static org.smoothbuild.lang.object.type.TestingTypes.array2A;
import static org.smoothbuild.lang.object.type.TestingTypes.array2B;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Blob;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Bool;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Nothing;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Person;
import static org.smoothbuild.lang.object.type.TestingTypes.array2String;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Type;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayA;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayB;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayBlob;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayBool;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayNothing;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayPerson;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayString;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayType;
import static org.smoothbuild.lang.object.type.TestingTypes.b;
import static org.smoothbuild.lang.object.type.TestingTypes.blob;
import static org.smoothbuild.lang.object.type.TestingTypes.bool;
import static org.smoothbuild.lang.object.type.TestingTypes.nothing;
import static org.smoothbuild.lang.object.type.TestingTypes.person;
import static org.smoothbuild.lang.object.type.TestingTypes.string;
import static org.smoothbuild.lang.object.type.TestingTypes.type;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;

import com.google.common.testing.EqualsTester;

public class TypeTest {
  @ParameterizedTest
  @MethodSource("names")
  public static void name(Type type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public static void quoted_name(Type type, String name) {
    assertThat(type.q())
        .isEqualTo("'" + name + "'");
  }

  @ParameterizedTest
  @MethodSource("names")
  public static void to_string(Type type, String name) {
    assertThat(type.toString())
        .isEqualTo("Type(\"" + name + "\")");
  }

  public static Stream<Arguments> names() {
    return Stream.of(
        arguments(type, "Type"),
        arguments(bool, "Bool"),
        arguments(string, "String"),
        arguments(blob, "Blob"),
        arguments(nothing, "Nothing"),
        arguments(person, "Person"),
        arguments(a, "a"),

        arguments(arrayType, "[Type]"),
        arguments(arrayBool, "[Bool]"),
        arguments(arrayString, "[String]"),
        arguments(arrayBlob, "[Blob]"),
        arguments(arrayNothing, "[Nothing]"),
        arguments(arrayPerson, "[Person]"),
        arguments(arrayA, "[a]"),

        arguments(array2Type, "[[Type]]"),
        arguments(array2Bool, "[[Bool]]"),
        arguments(array2String, "[[String]]"),
        arguments(array2Blob, "[[Blob]]"),
        arguments(array2Nothing, "[[Nothing]]"),
        arguments(array2Person, "[[Person]]"),
        arguments(array2A, "[[a]]")
    );
  }

  @ParameterizedTest
  @MethodSource("jType_test_data")
  public static void jType(Type type, Class<?> expected) {
    assertThat(type.jType())
        .isEqualTo(expected);
  }

  public static List<Arguments> jType_test_data() {
    return List.of(
        arguments(type, ConcreteType.class),
        arguments(bool, Bool.class),
        arguments(string, SString.class),
        arguments(blob, Blob.class),
        arguments(nothing, Nothing.class),
        arguments(a, SObject.class),
        arguments(arrayType, Array.class),
        arguments(arrayBool, Array.class),
        arguments(arrayString, Array.class),
        arguments(arrayBlob, Array.class),
        arguments(arrayNothing, Array.class),
        arguments(arrayA, Array.class)
    );
  }

  @ParameterizedTest
  @MethodSource("coreType_test_data")
  public static void coreType(Type type, Type expected) {
    assertThat(type.coreType())
        .isEqualTo(expected);
  }

  public static List<Arguments> coreType_test_data() {
    return List.of(
        arguments(type, type),
        arguments(bool, bool),
        arguments(string, string),
        arguments(blob, blob),
        arguments(nothing, nothing),
        arguments(person, person),
        arguments(a, a),

        arguments(arrayType, type),
        arguments(arrayBool, bool),
        arguments(arrayString, string),
        arguments(arrayBlob, blob),
        arguments(arrayNothing, nothing),
        arguments(arrayPerson, person),
        arguments(arrayA, a),

        arguments(array2Type, type),
        arguments(array2Bool, bool),
        arguments(array2String, string),
        arguments(array2Blob, blob),
        arguments(array2Nothing, nothing),
        arguments(array2Person, person),
        arguments(array2A, a)
    );
  }

  @ParameterizedTest
  @MethodSource("replaceCoreType_test_data")
  public static void replaceCoreType(Type type, Type coreType, Type expected) {
    assertThat(type.replaceCoreType(coreType))
        .isEqualTo(expected);
  }

  public static List<Arguments> replaceCoreType_test_data() {
    return List.of(
        arguments(type, type, type),
        arguments(type, bool, bool),
        arguments(type, string, string),
        arguments(type, blob, blob),
        arguments(type, nothing, nothing),
        arguments(type, person, person),
        arguments(type, a, a),

        arguments(bool, type, type),
        arguments(bool, bool, bool),
        arguments(bool, string, string),
        arguments(bool, blob, blob),
        arguments(bool, nothing, nothing),
        arguments(bool, person, person),
        arguments(bool, a, a),

        arguments(string, type, type),
        arguments(string, bool, bool),
        arguments(string, string, string),
        arguments(string, blob, blob),
        arguments(string, nothing, nothing),
        arguments(string, person, person),
        arguments(string, a, a),

        arguments(blob, type, type),
        arguments(blob, bool, bool),
        arguments(blob, string, string),
        arguments(blob, blob, blob),
        arguments(blob, nothing, nothing),
        arguments(blob, person, person),
        arguments(blob, a, a),

        arguments(nothing, type, type),
        arguments(nothing, bool, bool),
        arguments(nothing, string, string),
        arguments(nothing, blob, blob),
        arguments(nothing, nothing, nothing),
        arguments(nothing, person, person),
        arguments(nothing, a, a),

        arguments(person, type, type),
        arguments(person, bool, bool),
        arguments(person, string, string),
        arguments(person, blob, blob),
        arguments(person, nothing, nothing),
        arguments(person, person, person),
        arguments(person, a, a),

        //

        arguments(type, arrayType, arrayType),
        arguments(type, arrayBool, arrayBool),
        arguments(type, arrayString, arrayString),
        arguments(type, arrayBlob, arrayBlob),
        arguments(type, arrayNothing, arrayNothing),
        arguments(type, arrayPerson, arrayPerson),
        arguments(type, arrayA, arrayA),

        arguments(bool, arrayType, arrayType),
        arguments(bool, arrayBool, arrayBool),
        arguments(bool, arrayString, arrayString),
        arguments(bool, arrayBlob, arrayBlob),
        arguments(bool, arrayNothing, arrayNothing),
        arguments(bool, arrayPerson, arrayPerson),
        arguments(bool, arrayA, arrayA),

        arguments(string, arrayType, arrayType),
        arguments(string, arrayBool, arrayBool),
        arguments(string, arrayString, arrayString),
        arguments(string, arrayBlob, arrayBlob),
        arguments(string, arrayNothing, arrayNothing),
        arguments(string, arrayPerson, arrayPerson),
        arguments(string, arrayA, arrayA),

        arguments(blob, arrayType, arrayType),
        arguments(blob, arrayBool, arrayBool),
        arguments(blob, arrayString, arrayString),
        arguments(blob, arrayBlob, arrayBlob),
        arguments(blob, arrayNothing, arrayNothing),
        arguments(blob, arrayPerson, arrayPerson),
        arguments(blob, arrayA, arrayA),

        arguments(nothing, arrayType, arrayType),
        arguments(nothing, arrayBool, arrayBool),
        arguments(nothing, arrayString, arrayString),
        arguments(nothing, arrayBlob, arrayBlob),
        arguments(nothing, arrayNothing, arrayNothing),
        arguments(nothing, arrayPerson, arrayPerson),
        arguments(nothing, arrayA, arrayA),

        arguments(person, arrayType, arrayType),
        arguments(person, arrayBool, arrayBool),
        arguments(person, arrayString, arrayString),
        arguments(person, arrayBlob, arrayBlob),
        arguments(person, arrayNothing, arrayNothing),
        arguments(person, arrayPerson, arrayPerson),
        arguments(person, arrayA, arrayA),

        arguments(a, arrayType, arrayType),
        arguments(a, arrayBool, arrayBool),
        arguments(a, arrayString, arrayString),
        arguments(a, arrayBlob, arrayBlob),
        arguments(a, arrayNothing, arrayNothing),
        arguments(a, arrayPerson, arrayPerson),
        arguments(a, arrayA, arrayA),

        //

        arguments(arrayType, type, arrayType),
        arguments(arrayType, bool, arrayBool),
        arguments(arrayType, string, arrayString),
        arguments(arrayType, blob, arrayBlob),
        arguments(arrayType, nothing, arrayNothing),
        arguments(arrayType, person, arrayPerson),
        arguments(arrayType, a, arrayA),

        arguments(arrayBool, type, arrayType),
        arguments(arrayBool, bool, arrayBool),
        arguments(arrayBool, string, arrayString),
        arguments(arrayBool, blob, arrayBlob),
        arguments(arrayBool, nothing, arrayNothing),
        arguments(arrayBool, person, arrayPerson),
        arguments(arrayBool, a, arrayA),

        arguments(arrayString, type, arrayType),
        arguments(arrayString, bool, arrayBool),
        arguments(arrayString, string, arrayString),
        arguments(arrayString, blob, arrayBlob),
        arguments(arrayString, nothing, arrayNothing),
        arguments(arrayString, person, arrayPerson),
        arguments(arrayString, a, arrayA),

        arguments(arrayBlob, type, arrayType),
        arguments(arrayBlob, bool, arrayBool),
        arguments(arrayBlob, string, arrayString),
        arguments(arrayBlob, blob, arrayBlob),
        arguments(arrayBlob, nothing, arrayNothing),
        arguments(arrayBlob, person, arrayPerson),
        arguments(arrayBlob, a, arrayA),

        arguments(arrayNothing, type, arrayType),
        arguments(arrayNothing, bool, arrayBool),
        arguments(arrayNothing, string, arrayString),
        arguments(arrayNothing, blob, arrayBlob),
        arguments(arrayNothing, nothing, arrayNothing),
        arguments(arrayNothing, person, arrayPerson),
        arguments(arrayNothing, a, arrayA),

        arguments(arrayPerson, type, arrayType),
        arguments(arrayPerson, bool, arrayBool),
        arguments(arrayPerson, string, arrayString),
        arguments(arrayPerson, blob, arrayBlob),
        arguments(arrayPerson, nothing, arrayNothing),
        arguments(arrayPerson, person, arrayPerson),
        arguments(arrayPerson, a, arrayA),

        arguments(arrayA, type, arrayType),
        arguments(arrayA, bool, arrayBool),
        arguments(arrayA, string, arrayString),
        arguments(arrayA, blob, arrayBlob),
        arguments(arrayA, nothing, arrayNothing),
        arguments(arrayA, person, arrayPerson),
        arguments(arrayA, a, arrayA),

        //

        arguments(arrayType, arrayType, array2Type),
        arguments(arrayType, arrayBool, array2Bool),
        arguments(arrayType, arrayString, array2String),
        arguments(arrayType, arrayBlob, array2Blob),
        arguments(arrayType, arrayNothing, array2Nothing),
        arguments(arrayType, arrayPerson, array2Person),
        arguments(arrayType, arrayA, array2A),

        arguments(arrayBool, arrayType, array2Type),
        arguments(arrayBool, arrayBool, array2Bool),
        arguments(arrayBool, arrayString, array2String),
        arguments(arrayBool, arrayBlob, array2Blob),
        arguments(arrayBool, arrayNothing, array2Nothing),
        arguments(arrayBool, arrayPerson, array2Person),
        arguments(arrayBool, arrayA, array2A),

        arguments(arrayString, arrayType, array2Type),
        arguments(arrayString, arrayBool, array2Bool),
        arguments(arrayString, arrayString, array2String),
        arguments(arrayString, arrayBlob, array2Blob),
        arguments(arrayString, arrayNothing, array2Nothing),
        arguments(arrayString, arrayPerson, array2Person),
        arguments(arrayString, arrayA, array2A),

        arguments(arrayBlob, arrayType, array2Type),
        arguments(arrayBlob, arrayBool, array2Bool),
        arguments(arrayBlob, arrayString, array2String),
        arguments(arrayBlob, arrayBlob, array2Blob),
        arguments(arrayBlob, arrayNothing, array2Nothing),
        arguments(arrayBlob, arrayPerson, array2Person),
        arguments(arrayBlob, arrayA, array2A),

        arguments(arrayNothing, arrayType, array2Type),
        arguments(arrayNothing, arrayBool, array2Bool),
        arguments(arrayNothing, arrayString, array2String),
        arguments(arrayNothing, arrayBlob, array2Blob),
        arguments(arrayNothing, arrayNothing, array2Nothing),
        arguments(arrayNothing, arrayPerson, array2Person),
        arguments(arrayNothing, arrayA, array2A),

        arguments(arrayPerson, arrayType, array2Type),
        arguments(arrayPerson, arrayBool, array2Bool),
        arguments(arrayPerson, arrayString, array2String),
        arguments(arrayPerson, arrayBlob, array2Blob),
        arguments(arrayPerson, arrayNothing, array2Nothing),
        arguments(arrayPerson, arrayPerson, array2Person),
        arguments(arrayPerson, arrayA, array2A),

        arguments(arrayA, arrayType, array2Type),
        arguments(arrayA, arrayBool, array2Bool),
        arguments(arrayA, arrayString, array2String),
        arguments(arrayA, arrayBlob, array2Blob),
        arguments(arrayA, arrayNothing, array2Nothing),
        arguments(arrayA, arrayPerson, array2Person),
        arguments(arrayA, arrayA, array2A)
    );
  }

  @ParameterizedTest
  @MethodSource("coreDepth_test_data")
  public static void coreDepth(Type type, int expected) {
    assertThat(type.coreDepth())
        .isEqualTo(expected);
  }

  public static List<Arguments> coreDepth_test_data() {
    return List.of(
        arguments(type, 0),
        arguments(bool, 0),
        arguments(string, 0),
        arguments(blob, 0),
        arguments(nothing, 0),
        arguments(person, 0),
        arguments(a, 0),

        arguments(arrayType, 1),
        arguments(arrayBool, 1),
        arguments(arrayString, 1),
        arguments(arrayBlob, 1),
        arguments(arrayNothing, 1),
        arguments(arrayPerson, 1),
        arguments(arrayA, 1),

        arguments(array2Type, 2),
        arguments(array2Bool, 2),
        arguments(array2String, 2),
        arguments(array2Blob, 2),
        arguments(array2Nothing, 2),
        arguments(array2Person, 2),
        arguments(array2A, 2));
  }

  @ParameterizedTest
  @MethodSource("changeCoreDepthBy_test_data_with_illegal_values")
  public void changeCoreDepthBy_fails_for(Type type, int change) {
    assertCall(() -> type.changeCoreDepthBy(change))
        .throwsException(IllegalArgumentException.class);
  }

  public static List<Arguments> changeCoreDepthBy_test_data_with_illegal_values() {
    return List.of(
        arguments(type, -2),
        arguments(bool, -2),
        arguments(string, -2),
        arguments(nothing, -2),
        arguments(person, -2),
        arguments(a, -2),

        arguments(type, -1),
        arguments(bool, -1),
        arguments(string, -1),
        arguments(nothing, -1),
        arguments(person, -1),
        arguments(a, -1),

        //
        arguments(arrayType, -2),
        arguments(arrayBool, -2),
        arguments(arrayString, -2),
        arguments(arrayNothing, -2),
        arguments(arrayPerson, -2),
        arguments(arrayA, -2),

        //
        arguments(array2Type, -3),
        arguments(array2Bool, -3),
        arguments(array2String, -3),
        arguments(array2Blob, -3),
        arguments(array2Nothing, -3),
        arguments(array2Person, -3),
        arguments(array2A, -3)
    );
  }

  @ParameterizedTest
  @MethodSource("changeCoreDepth_test_data")
  public void changeCoreDepthBy(Type type, int change, Type expected) {
    assertThat(type.changeCoreDepthBy(change))
        .isEqualTo(expected);
  }

  public static List<Arguments> changeCoreDepth_test_data() {
    return List.of(
        arguments(type, 0, type),
        arguments(bool, 0, bool),
        arguments(string, 0, string),
        arguments(nothing, 0, nothing),
        arguments(person, 0, person),
        arguments(a, 0, a),

        arguments(type, 1, arrayType),
        arguments(bool, 1, arrayBool),
        arguments(string, 1, arrayString),
        arguments(nothing, 1, arrayNothing),
        arguments(person, 1, arrayPerson),
        arguments(a, 1, arrayA),

        arguments(type, 2, array2Type),
        arguments(bool, 2, array2Bool),
        arguments(string, 2, array2String),
        arguments(nothing, 2, array2Nothing),
        arguments(person, 2, array2Person),
        arguments(a, 2, array2A),

        //
        arguments(arrayType, -1, type),
        arguments(arrayBool, -1, bool),
        arguments(arrayString, -1, string),
        arguments(arrayBlob, -1, blob),
        arguments(arrayNothing, -1, nothing),
        arguments(arrayPerson, -1, person),
        arguments(arrayA, -1, a),

        arguments(arrayType, 0, arrayType),
        arguments(arrayBool, 0, arrayBool),
        arguments(arrayString, 0, arrayString),
        arguments(arrayBlob, 0, arrayBlob),
        arguments(arrayNothing, 0, arrayNothing),
        arguments(arrayPerson, 0, arrayPerson),
        arguments(arrayA, 0, arrayA),

        arguments(arrayType, 1, array2Type),
        arguments(arrayBool, 1, array2Bool),
        arguments(arrayString, 1, array2String),
        arguments(arrayBlob, 1, array2Blob),
        arguments(arrayNothing, 1, array2Nothing),
        arguments(arrayPerson, 1, array2Person),
        arguments(arrayA, 1, array2A),

        //
        arguments(array2Type, -2, type),
        arguments(array2Bool, -2, bool),
        arguments(array2String, -2, string),
        arguments(array2Blob, -2, blob),
        arguments(array2Nothing, -2, nothing),
        arguments(array2Person, -2, person),
        arguments(array2A, -2, a),

        arguments(array2Type, -1, arrayType),
        arguments(array2Bool, -1, arrayBool),
        arguments(array2String, -1, arrayString),
        arguments(array2Blob, -1, arrayBlob),
        arguments(array2Nothing, -1, arrayNothing),
        arguments(array2Person, -1, arrayPerson),
        arguments(array2A, -1, arrayA),

        arguments(array2Type, 0, array2Type),
        arguments(array2Bool, 0, array2Bool),
        arguments(array2String, 0, array2String),
        arguments(array2Blob, 0, array2Blob),
        arguments(array2Nothing, 0, array2Nothing),
        arguments(array2Person, 0, array2Person),
        arguments(array2A, 0, array2A)
    );
  }

  @ParameterizedTest
  @MethodSource("isGeneric_test_data")
  public void isGeneric(Type type, boolean expected) {
    assertThat(type.isGeneric())
        .isEqualTo(expected);
  }

  public static List<Arguments> isGeneric_test_data() {
    return List.of(
        arguments(type, false),
        arguments(bool, false),
        arguments(string, false),
        arguments(blob, false),
        arguments(nothing, false),
        arguments(person, false),
        arguments(arrayType, false),
        arguments(arrayBool, false),
        arguments(arrayString, false),
        arguments(arrayBlob, false),
        arguments(arrayNothing, false),
        arguments(arrayPerson, false),
        arguments(array2Type, false),
        arguments(array2Bool, false),
        arguments(array2String, false),
        arguments(array2Blob, false),
        arguments(array2Nothing, false),
        arguments(array2Person, false),

        arguments(a, true),
        arguments(arrayA, true),
        arguments(array2A, true),
        arguments(b, true),
        arguments(arrayB, true),
        arguments(array2B, true)
    );
  }

  @ParameterizedTest
  @MethodSource("isArray_test_data")
  public void isArray(Type type, boolean expected) {
    assertThat(type.isArray())
        .isEqualTo(expected);
  }

  public static List<Arguments> isArray_test_data() {
    return List.of(
        arguments(type, false),
        arguments(bool, false),
        arguments(string, false),
        arguments(blob, false),
        arguments(nothing, false),
        arguments(person, false),
        arguments(a, false),

        arguments(arrayType, true),
        arguments(arrayString, true),
        arguments(arrayBool, true),
        arguments(arrayBlob, true),
        arguments(arrayNothing, true),
        arguments(arrayPerson, true),
        arguments(arrayA, true),
        arguments(array2Type, true),
        arguments(array2Bool, true),
        arguments(array2String, true),
        arguments(array2Blob, true),
        arguments(array2Nothing, true),
        arguments(array2Person, true),
        arguments(array2A, true)
    );
  }

  @ParameterizedTest
  @MethodSource("superType_test_data")
  public void superType(Type type, Type expected) {
    assertThat(type.superType())
        .isEqualTo(expected);
  }

  public static List<Arguments> superType_test_data() {
    return List.of(
        arguments(type, null),
        arguments(bool, null),
        arguments(string, null),
        arguments(blob, null),
        arguments(nothing, null),
        arguments(person, string),
        arguments(a, null),

        arguments(arrayType, null),
        arguments(arrayBool, null),
        arguments(arrayString, null),
        arguments(arrayBlob, null),
        arguments(arrayNothing, null),
        arguments(arrayPerson, arrayString),
        arguments(arrayA, null),

        arguments(array2Type, null),
        arguments(array2Bool, null),
        arguments(array2String, null),
        arguments(array2Blob, null),
        arguments(array2Nothing, null),
        arguments(array2Person, array2String),
        arguments(array2A, null)
    );
  }

  @ParameterizedTest
  @MethodSource("hierarchy_test_data")
  public void hierarchy(List<Type> hierarchy) {
    Type root = hierarchy.get(hierarchy.size() - 1);
    assertThat(root.hierarchy())
        .isEqualTo(hierarchy);
  }

  public static List<Arguments> hierarchy_test_data() {
    return List.of(
        arguments(list(type)),
        arguments(list(string)),
        arguments(list(bool)),
        arguments(list(string, person)),
        arguments(list(nothing)),
        arguments(list(a)),
        arguments(list(arrayType)),
        arguments(list(arrayBool)),
        arguments(list(arrayString)),
        arguments(list(arrayString, arrayPerson)),
        arguments(list(arrayNothing)),
        arguments(list(arrayA)),
        arguments(list(array2Type)),
        arguments(list(array2Bool)),
        arguments(list(array2String)),
        arguments(list(array2String, array2Person)),
        arguments(list(array2Nothing)),
        arguments(list(array2A)));
  }

  @ParameterizedTest
  @MethodSource("isAssignableFrom_test_data")
  public void isAssignableFrom(Type destination, Type source, boolean expected) {
    assertThat(destination.isAssignableFrom(source))
        .isEqualTo(expected);
  }

  public static List<Arguments> isAssignableFrom_test_data() {
    return List.of(
        arguments(type, type, true),
        arguments(type, bool, false),
        arguments(type, string, false),
        arguments(type, blob, false),
        arguments(type, person, false),
        arguments(type, nothing, true),
        arguments(type, a, false),
        arguments(type, arrayType, false),
        arguments(type, arrayBool, false),
        arguments(type, arrayString, false),
        arguments(type, arrayBlob, false),
        arguments(type, arrayPerson, false),
        arguments(type, arrayNothing, false),
        arguments(type, arrayA, false),
        arguments(type, array2Type, false),
        arguments(type, array2Bool, false),
        arguments(type, array2String, false),
        arguments(type, array2Blob, false),
        arguments(type, array2Person, false),
        arguments(type, array2Nothing, false),
        arguments(type, array2A, false),

        arguments(bool, type, false),
        arguments(bool, bool, true),
        arguments(bool, string, false),
        arguments(bool, blob, false),
        arguments(bool, person, false),
        arguments(bool, nothing, true),
        arguments(bool, a, false),
        arguments(bool, arrayType, false),
        arguments(bool, arrayBool, false),
        arguments(bool, arrayString, false),
        arguments(bool, arrayBlob, false),
        arguments(bool, arrayPerson, false),
        arguments(bool, arrayNothing, false),
        arguments(bool, arrayA, false),
        arguments(bool, array2Type, false),
        arguments(bool, array2Bool, false),
        arguments(bool, array2String, false),
        arguments(bool, array2Blob, false),
        arguments(bool, array2Person, false),
        arguments(bool, array2Nothing, false),
        arguments(bool, array2A, false),

        arguments(string, type, false),
        arguments(string, bool, false),
        arguments(string, string, true),
        arguments(string, blob, false),
        arguments(string, person, true),
        arguments(string, nothing, true),
        arguments(string, a, false),
        arguments(string, arrayType, false),
        arguments(string, arrayBool, false),
        arguments(string, arrayString, false),
        arguments(string, arrayBlob, false),
        arguments(string, arrayPerson, false),
        arguments(string, arrayNothing, false),
        arguments(string, arrayA, false),
        arguments(string, array2Type, false),
        arguments(string, array2Bool, false),
        arguments(string, array2String, false),
        arguments(string, array2Blob, false),
        arguments(string, array2Person, false),
        arguments(string, array2Nothing, false),
        arguments(string, array2A, false),

        arguments(blob, type, false),
        arguments(blob, bool, false),
        arguments(blob, string, false),
        arguments(blob, blob, true),
        arguments(blob, person, false),
        arguments(blob, nothing, true),
        arguments(blob, a, false),
        arguments(blob, arrayType, false),
        arguments(blob, arrayBool, false),
        arguments(blob, arrayString, false),
        arguments(blob, arrayBlob, false),
        arguments(blob, arrayPerson, false),
        arguments(blob, arrayNothing, false),
        arguments(blob, arrayA, false),
        arguments(blob, array2Type, false),
        arguments(blob, array2Bool, false),
        arguments(blob, array2String, false),
        arguments(blob, array2Blob, false),
        arguments(blob, array2Person, false),
        arguments(blob, array2Nothing, false),
        arguments(blob, array2A, false),

        arguments(person, type, false),
        arguments(person, bool, false),
        arguments(person, string, false),
        arguments(person, blob, false),
        arguments(person, person, true),
        arguments(person, nothing, true),
        arguments(person, a, false),
        arguments(person, arrayType, false),
        arguments(person, arrayBool, false),
        arguments(person, arrayString, false),
        arguments(person, arrayBlob, false),
        arguments(person, arrayPerson, false),
        arguments(person, arrayNothing, false),
        arguments(person, arrayA, false),
        arguments(person, array2Type, false),
        arguments(person, array2Bool, false),
        arguments(person, array2String, false),
        arguments(person, array2Blob, false),
        arguments(person, array2Person, false),
        arguments(person, array2Nothing, false),
        arguments(person, array2A, false),

        arguments(nothing, type, false),
        arguments(nothing, bool, false),
        arguments(nothing, string, false),
        arguments(nothing, blob, false),
        arguments(nothing, person, false),
        arguments(nothing, nothing, true),
        arguments(nothing, a, false),
        arguments(nothing, arrayType, false),
        arguments(nothing, arrayBool, false),
        arguments(nothing, arrayString, false),
        arguments(nothing, arrayBlob, false),
        arguments(nothing, arrayPerson, false),
        arguments(nothing, arrayNothing, false),
        arguments(nothing, arrayA, false),
        arguments(nothing, array2Type, false),
        arguments(nothing, array2Bool, false),
        arguments(nothing, array2String, false),
        arguments(nothing, array2Blob, false),
        arguments(nothing, array2Person, false),
        arguments(nothing, array2Nothing, false),
        arguments(nothing, array2A, false),

        arguments(a, type, false),
        arguments(a, bool, false),
        arguments(a, string, false),
        arguments(a, blob, false),
        arguments(a, person, false),
        arguments(a, nothing, true),
        arguments(a, a, true),
        arguments(a, b, false),
        arguments(a, arrayType, false),
        arguments(a, arrayBool, false),
        arguments(a, arrayString, false),
        arguments(a, arrayBlob, false),
        arguments(a, arrayPerson, false),
        arguments(a, arrayNothing, false),
        arguments(a, arrayA, false),
        arguments(a, arrayB, false),
        arguments(a, array2Type, false),
        arguments(a, array2Bool, false),
        arguments(a, array2String, false),
        arguments(a, array2Blob, false),
        arguments(a, array2Person, false),
        arguments(a, array2Nothing, false),
        arguments(a, array2A, false),
        arguments(a, array2B, false),

        arguments(arrayType, type, false),
        arguments(arrayType, bool, false),
        arguments(arrayType, string, false),
        arguments(arrayType, blob, false),
        arguments(arrayType, person, false),
        arguments(arrayType, nothing, true),
        arguments(arrayType, a, false),
        arguments(arrayType, arrayType, true),
        arguments(arrayType, arrayBool, false),
        arguments(arrayType, arrayString, false),
        arguments(arrayType, arrayBlob, false),
        arguments(arrayType, arrayPerson, false),
        arguments(arrayType, arrayNothing, true),
        arguments(arrayType, arrayA, false),
        arguments(arrayType, array2Type, false),
        arguments(arrayType, array2Bool, false),
        arguments(arrayType, array2String, false),
        arguments(arrayType, array2Blob, false),
        arguments(arrayType, array2Person, false),
        arguments(arrayType, array2Nothing, false),
        arguments(arrayType, array2A, false),

        arguments(arrayString, type, false),
        arguments(arrayString, bool, false),
        arguments(arrayString, string, false),
        arguments(arrayString, blob, false),
        arguments(arrayString, person, false),
        arguments(arrayString, nothing, true),
        arguments(arrayString, a, false),
        arguments(arrayString, arrayType, false),
        arguments(arrayString, arrayBool, false),
        arguments(arrayString, arrayString, true),
        arguments(arrayString, arrayBlob, false),
        arguments(arrayString, arrayPerson, true),
        arguments(arrayString, arrayNothing, true),
        arguments(arrayString, arrayA, false),
        arguments(arrayString, array2Type, false),
        arguments(arrayString, array2Bool, false),
        arguments(arrayString, array2String, false),
        arguments(arrayString, array2Blob, false),
        arguments(arrayString, array2Person, false),
        arguments(arrayString, array2Nothing, false),
        arguments(arrayString, array2A, false),

        arguments(arrayBool, type, false),
        arguments(arrayBool, bool, false),
        arguments(arrayBool, string, false),
        arguments(arrayBool, blob, false),
        arguments(arrayBool, person, false),
        arguments(arrayBool, nothing, true),
        arguments(arrayBool, a, false),
        arguments(arrayBool, arrayType, false),
        arguments(arrayBool, arrayBool, true),
        arguments(arrayBool, arrayString, false),
        arguments(arrayBool, arrayBlob, false),
        arguments(arrayBool, arrayPerson, false),
        arguments(arrayBool, arrayNothing, true),
        arguments(arrayBool, arrayA, false),
        arguments(arrayBool, array2Type, false),
        arguments(arrayBool, array2Bool, false),
        arguments(arrayBool, array2String, false),
        arguments(arrayBool, array2Blob, false),
        arguments(arrayBool, array2Person, false),
        arguments(arrayBool, array2Nothing, false),
        arguments(arrayBool, array2A, false),

        arguments(arrayBlob, type, false),
        arguments(arrayBlob, bool, false),
        arguments(arrayBlob, string, false),
        arguments(arrayBlob, blob, false),
        arguments(arrayBlob, person, false),
        arguments(arrayBlob, nothing, true),
        arguments(arrayBlob, a, false),
        arguments(arrayBlob, arrayType, false),
        arguments(arrayBlob, arrayBool, false),
        arguments(arrayBlob, arrayString, false),
        arguments(arrayBlob, arrayBlob, true),
        arguments(arrayBlob, arrayPerson, false),
        arguments(arrayBlob, arrayNothing, true),
        arguments(arrayBlob, arrayA, false),
        arguments(arrayBlob, array2Type, false),
        arguments(arrayBlob, array2Bool, false),
        arguments(arrayBlob, array2String, false),
        arguments(arrayBlob, array2Blob, false),
        arguments(arrayBlob, array2Person, false),
        arguments(arrayBlob, array2Nothing, false),
        arguments(arrayBlob, array2A, false),

        arguments(arrayPerson, type, false),
        arguments(arrayPerson, bool, false),
        arguments(arrayPerson, string, false),
        arguments(arrayPerson, blob, false),
        arguments(arrayPerson, person, false),
        arguments(arrayPerson, nothing, true),
        arguments(arrayPerson, a, false),
        arguments(arrayPerson, arrayType, false),
        arguments(arrayPerson, arrayBool, false),
        arguments(arrayPerson, arrayString, false),
        arguments(arrayPerson, arrayBlob, false),
        arguments(arrayPerson, arrayPerson, true),
        arguments(arrayPerson, arrayNothing, true),
        arguments(arrayPerson, arrayA, false),
        arguments(arrayPerson, array2Type, false),
        arguments(arrayPerson, array2Bool, false),
        arguments(arrayPerson, array2String, false),
        arguments(arrayPerson, array2Blob, false),
        arguments(arrayPerson, array2Person, false),
        arguments(arrayPerson, array2Nothing, false),
        arguments(arrayPerson, array2A, false),

        arguments(arrayNothing, type, false),
        arguments(arrayNothing, bool, false),
        arguments(arrayNothing, string, false),
        arguments(arrayNothing, blob, false),
        arguments(arrayNothing, person, false),
        arguments(arrayNothing, nothing, true),
        arguments(arrayNothing, a, false),
        arguments(arrayNothing, arrayType, false),
        arguments(arrayNothing, arrayBool, false),
        arguments(arrayNothing, arrayString, false),
        arguments(arrayNothing, arrayBlob, false),
        arguments(arrayNothing, arrayPerson, false),
        arguments(arrayNothing, arrayNothing, true),
        arguments(arrayNothing, arrayA, false),
        arguments(arrayNothing, array2Type, false),
        arguments(arrayNothing, array2Bool, false),
        arguments(arrayNothing, array2String, false),
        arguments(arrayNothing, array2Blob, false),
        arguments(arrayNothing, array2Person, false),
        arguments(arrayNothing, array2Nothing, false),
        arguments(arrayNothing, array2A, false),

        arguments(arrayA, type, false),
        arguments(arrayA, bool, false),
        arguments(arrayA, string, false),
        arguments(arrayA, blob, false),
        arguments(arrayA, person, false),
        arguments(arrayA, nothing, true),
        arguments(arrayA, a, false),
        arguments(arrayA, b, false),
        arguments(arrayA, arrayType, false),
        arguments(arrayA, arrayBool, false),
        arguments(arrayA, arrayString, false),
        arguments(arrayA, arrayBlob, false),
        arguments(arrayA, arrayPerson, false),
        arguments(arrayA, arrayNothing, true),
        arguments(arrayA, arrayA, true),
        arguments(arrayA, arrayB, false),
        arguments(arrayA, array2Type, false),
        arguments(arrayA, array2Bool, false),
        arguments(arrayA, array2String, false),
        arguments(arrayA, array2Blob, false),
        arguments(arrayA, array2Person, false),
        arguments(arrayA, array2Nothing, false),
        arguments(arrayA, array2A, false),
        arguments(arrayA, array2B, false),

        arguments(array2Type, type, false),
        arguments(array2Type, bool, false),
        arguments(array2Type, string, false),
        arguments(array2Type, blob, false),
        arguments(array2Type, person, false),
        arguments(array2Type, nothing, true),
        arguments(array2Type, a, false),
        arguments(array2Type, arrayType, false),
        arguments(array2Type, arrayBool, false),
        arguments(array2Type, arrayString, false),
        arguments(array2Type, arrayBlob, false),
        arguments(array2Type, arrayPerson, false),
        arguments(array2Type, arrayNothing, true),
        arguments(array2Type, arrayA, false),
        arguments(array2Type, array2Type, true),
        arguments(array2Type, array2Bool, false),
        arguments(array2Type, array2String, false),
        arguments(array2Type, array2Blob, false),
        arguments(array2Type, array2Person, false),
        arguments(array2Type, array2Nothing, true),
        arguments(array2Type, array2A, false),

        arguments(array2Bool, type, false),
        arguments(array2Bool, bool, false),
        arguments(array2Bool, string, false),
        arguments(array2Bool, blob, false),
        arguments(array2Bool, person, false),
        arguments(array2Bool, nothing, true),
        arguments(array2Bool, a, false),
        arguments(array2Bool, arrayType, false),
        arguments(array2Bool, arrayBool, false),
        arguments(array2Bool, arrayString, false),
        arguments(array2Bool, arrayBlob, false),
        arguments(array2Bool, arrayPerson, false),
        arguments(array2Bool, arrayNothing, true),
        arguments(array2Bool, arrayA, false),
        arguments(array2Bool, array2Type, false),
        arguments(array2Bool, array2Bool, true),
        arguments(array2Bool, array2String, false),
        arguments(array2Bool, array2Blob, false),
        arguments(array2Bool, array2Person, false),
        arguments(array2Bool, array2Nothing, true),
        arguments(array2Bool, array2A, false),

        arguments(array2String, type, false),
        arguments(array2String, bool, false),
        arguments(array2String, string, false),
        arguments(array2String, blob, false),
        arguments(array2String, person, false),
        arguments(array2String, nothing, true),
        arguments(array2String, a, false),
        arguments(array2String, arrayType, false),
        arguments(array2String, arrayBool, false),
        arguments(array2String, arrayString, false),
        arguments(array2String, arrayBlob, false),
        arguments(array2String, arrayPerson, false),
        arguments(array2String, arrayNothing, true),
        arguments(array2String, arrayA, false),
        arguments(array2String, array2Type, false),
        arguments(array2String, array2Bool, false),
        arguments(array2String, array2String, true),
        arguments(array2String, array2Blob, false),
        arguments(array2String, array2Person, true),
        arguments(array2String, array2Nothing, true),
        arguments(array2String, array2A, false),

        arguments(array2Blob, type, false),
        arguments(array2Blob, bool, false),
        arguments(array2Blob, string, false),
        arguments(array2Blob, blob, false),
        arguments(array2Blob, person, false),
        arguments(array2Blob, nothing, true),
        arguments(array2Blob, a, false),
        arguments(array2Blob, arrayType, false),
        arguments(array2Blob, arrayBool, false),
        arguments(array2Blob, arrayString, false),
        arguments(array2Blob, arrayBlob, false),
        arguments(array2Blob, arrayPerson, false),
        arguments(array2Blob, arrayNothing, true),
        arguments(array2Blob, arrayA, false),
        arguments(array2Blob, array2Type, false),
        arguments(array2Blob, array2Bool, false),
        arguments(array2Blob, array2String, false),
        arguments(array2Blob, array2Blob, true),
        arguments(array2Blob, array2Person, false),
        arguments(array2Blob, array2Nothing, true),
        arguments(array2Blob, array2A, false),

        arguments(array2Person, type, false),
        arguments(array2Person, bool, false),
        arguments(array2Person, string, false),
        arguments(array2Person, blob, false),
        arguments(array2Person, person, false),
        arguments(array2Person, nothing, true),
        arguments(array2Person, a, false),
        arguments(array2Person, arrayType, false),
        arguments(array2Person, arrayBool, false),
        arguments(array2Person, arrayString, false),
        arguments(array2Person, arrayBlob, false),
        arguments(array2Person, arrayPerson, false),
        arguments(array2Person, arrayNothing, true),
        arguments(array2Person, arrayA, false),
        arguments(array2Person, array2Type, false),
        arguments(array2Person, array2Bool, false),
        arguments(array2Person, array2String, false),
        arguments(array2Person, array2Blob, false),
        arguments(array2Person, array2Person, true),
        arguments(array2Person, array2Nothing, true),
        arguments(array2Person, array2A, false),

        arguments(array2Nothing, type, false),
        arguments(array2Nothing, bool, false),
        arguments(array2Nothing, string, false),
        arguments(array2Nothing, blob, false),
        arguments(array2Nothing, person, false),
        arguments(array2Nothing, nothing, true),
        arguments(array2Nothing, a, false),
        arguments(array2Nothing, arrayType, false),
        arguments(array2Nothing, arrayBool, false),
        arguments(array2Nothing, arrayString, false),
        arguments(array2Nothing, arrayBlob, false),
        arguments(array2Nothing, arrayPerson, false),
        arguments(array2Nothing, arrayNothing, true),
        arguments(array2Nothing, arrayA, false),
        arguments(array2Nothing, array2Type, false),
        arguments(array2Nothing, array2Bool, false),
        arguments(array2Nothing, array2String, false),
        arguments(array2Nothing, array2Blob, false),
        arguments(array2Nothing, array2Person, false),
        arguments(array2Nothing, array2Nothing, true),
        arguments(array2Nothing, array2A, false),

        arguments(array2A, type, false),
        arguments(array2A, bool, false),
        arguments(array2A, string, false),
        arguments(array2A, blob, false),
        arguments(array2A, person, false),
        arguments(array2A, nothing, true),
        arguments(array2A, a, false),
        arguments(array2A, b, false),
        arguments(array2A, arrayType, false),
        arguments(array2A, arrayBool, false),
        arguments(array2A, arrayString, false),
        arguments(array2A, arrayBlob, false),
        arguments(array2A, arrayPerson, false),
        arguments(array2A, arrayNothing, true),
        arguments(array2A, arrayA, false),
        arguments(array2A, arrayB, false),
        arguments(array2A, array2Type, false),
        arguments(array2A, array2Bool, false),
        arguments(array2A, array2String, false),
        arguments(array2A, array2Blob, false),
        arguments(array2A, array2Person, false),
        arguments(array2A, array2Nothing, true),
        arguments(array2A, array2A, true),
        arguments(array2A, array2B, false)
    );
  }

  @ParameterizedTest
  @MethodSource("isParamAssignableFrom_test_data")
  public void isParamAssignableFrom(Type destination, Type source, boolean expected) {
    assertThat(destination.isParamAssignableFrom(source))
        .isEqualTo(expected);
  }

  public static List<Arguments> isParamAssignableFrom_test_data() {
    return List.of(
        arguments(type, type, true),
        arguments(type, bool, false),
        arguments(type, string, false),
        arguments(type, blob, false),
        arguments(type, person, false),
        arguments(type, nothing, true),
        arguments(type, a, false),
        arguments(type, arrayType, false),
        arguments(type, arrayBool, false),
        arguments(type, arrayString, false),
        arguments(type, arrayBlob, false),
        arguments(type, arrayPerson, false),
        arguments(type, arrayNothing, false),
        arguments(type, arrayA, false),
        arguments(type, array2Type, false),
        arguments(type, array2Bool, false),
        arguments(type, array2String, false),
        arguments(type, array2Blob, false),
        arguments(type, array2Person, false),
        arguments(type, array2Nothing, false),
        arguments(type, array2A, false),

        arguments(bool, type, false),
        arguments(bool, bool, true),
        arguments(bool, string, false),
        arguments(bool, blob, false),
        arguments(bool, person, false),
        arguments(bool, nothing, true),
        arguments(bool, a, false),
        arguments(bool, arrayType, false),
        arguments(bool, arrayBool, false),
        arguments(bool, arrayString, false),
        arguments(bool, arrayBlob, false),
        arguments(bool, arrayPerson, false),
        arguments(bool, arrayNothing, false),
        arguments(bool, arrayA, false),
        arguments(bool, array2Type, false),
        arguments(bool, array2Bool, false),
        arguments(bool, array2String, false),
        arguments(bool, array2Blob, false),
        arguments(bool, array2Person, false),
        arguments(bool, array2Nothing, false),
        arguments(bool, array2A, false),

        arguments(string, type, false),
        arguments(string, bool, false),
        arguments(string, string, true),
        arguments(string, blob, false),
        arguments(string, person, true),
        arguments(string, nothing, true),
        arguments(string, a, false),
        arguments(string, arrayType, false),
        arguments(string, arrayBool, false),
        arguments(string, arrayString, false),
        arguments(string, arrayBlob, false),
        arguments(string, arrayPerson, false),
        arguments(string, arrayNothing, false),
        arguments(string, arrayA, false),
        arguments(string, array2Type, false),
        arguments(string, array2Bool, false),
        arguments(string, array2String, false),
        arguments(string, array2Blob, false),
        arguments(string, array2Person, false),
        arguments(string, array2Nothing, false),
        arguments(string, array2A, false),

        arguments(blob, type, false),
        arguments(blob, bool, false),
        arguments(blob, string, false),
        arguments(blob, blob, true),
        arguments(blob, person, false),
        arguments(blob, nothing, true),
        arguments(blob, a, false),
        arguments(blob, arrayType, false),
        arguments(blob, arrayBool, false),
        arguments(blob, arrayString, false),
        arguments(blob, arrayBlob, false),
        arguments(blob, arrayPerson, false),
        arguments(blob, arrayNothing, false),
        arguments(blob, arrayA, false),
        arguments(blob, array2Type, false),
        arguments(blob, array2Bool, false),
        arguments(blob, array2String, false),
        arguments(blob, array2Blob, false),
        arguments(blob, array2Person, false),
        arguments(blob, array2Nothing, false),
        arguments(blob, array2A, false),

        arguments(person, type, false),
        arguments(person, bool, false),
        arguments(person, string, false),
        arguments(person, blob, false),
        arguments(person, person, true),
        arguments(person, nothing, true),
        arguments(person, a, false),
        arguments(person, arrayType, false),
        arguments(person, arrayBool, false),
        arguments(person, arrayString, false),
        arguments(person, arrayBlob, false),
        arguments(person, arrayPerson, false),
        arguments(person, arrayNothing, false),
        arguments(person, arrayA, false),
        arguments(person, array2Type, false),
        arguments(person, array2Bool, false),
        arguments(person, array2String, false),
        arguments(person, array2Blob, false),
        arguments(person, array2Person, false),
        arguments(person, array2Nothing, false),
        arguments(person, array2A, false),

        arguments(nothing, type, false),
        arguments(nothing, bool, false),
        arguments(nothing, string, false),
        arguments(nothing, blob, false),
        arguments(nothing, person, false),
        arguments(nothing, nothing, true),
        arguments(nothing, a, false),
        arguments(nothing, arrayType, false),
        arguments(nothing, arrayBool, false),
        arguments(nothing, arrayString, false),
        arguments(nothing, arrayBlob, false),
        arguments(nothing, arrayPerson, false),
        arguments(nothing, arrayNothing, false),
        arguments(nothing, arrayA, false),
        arguments(nothing, array2Type, false),
        arguments(nothing, array2Bool, false),
        arguments(nothing, array2String, false),
        arguments(nothing, array2Blob, false),
        arguments(nothing, array2Person, false),
        arguments(nothing, array2Nothing, false),
        arguments(nothing, array2A, false),

        arguments(a, type, true),
        arguments(a, bool, true),
        arguments(a, string, true),
        arguments(a, blob, true),
        arguments(a, person, true),
        arguments(a, nothing, true),
        arguments(a, a, true),
        arguments(a, b, true),
        arguments(a, arrayType, true),
        arguments(a, arrayBool, true),
        arguments(a, arrayString, true),
        arguments(a, arrayBlob, true),
        arguments(a, arrayPerson, true),
        arguments(a, arrayNothing, true),
        arguments(a, arrayA, true),
        arguments(a, arrayB, true),
        arguments(a, array2Type, true),
        arguments(a, array2Bool, true),
        arguments(a, array2String, true),
        arguments(a, array2Blob, true),
        arguments(a, array2Person, true),
        arguments(a, array2Nothing, true),
        arguments(a, array2A, true),
        arguments(a, array2B, true),

        arguments(arrayType, type, false),
        arguments(arrayType, bool, false),
        arguments(arrayType, string, false),
        arguments(arrayType, blob, false),
        arguments(arrayType, person, false),
        arguments(arrayType, nothing, true),
        arguments(arrayType, a, false),
        arguments(arrayType, arrayType, true),
        arguments(arrayType, arrayBool, false),
        arguments(arrayType, arrayString, false),
        arguments(arrayType, arrayBlob, false),
        arguments(arrayType, arrayPerson, false),
        arguments(arrayType, arrayNothing, true),
        arguments(arrayType, arrayA, false),
        arguments(arrayType, array2Type, false),
        arguments(arrayType, array2Bool, false),
        arguments(arrayType, array2String, false),
        arguments(arrayType, array2Blob, false),
        arguments(arrayType, array2Person, false),
        arguments(arrayType, array2Nothing, false),
        arguments(arrayType, array2A, false),

        arguments(arrayBool, type, false),
        arguments(arrayBool, bool, false),
        arguments(arrayBool, string, false),
        arguments(arrayBool, blob, false),
        arguments(arrayBool, person, false),
        arguments(arrayBool, nothing, true),
        arguments(arrayBool, a, false),
        arguments(arrayBool, arrayType, false),
        arguments(arrayBool, arrayBool, true),
        arguments(arrayBool, arrayString, false),
        arguments(arrayBool, arrayBlob, false),
        arguments(arrayBool, arrayPerson, false),
        arguments(arrayBool, arrayNothing, true),
        arguments(arrayBool, arrayA, false),
        arguments(arrayBool, array2Type, false),
        arguments(arrayBool, array2Bool, false),
        arguments(arrayBool, array2String, false),
        arguments(arrayBool, array2Blob, false),
        arguments(arrayBool, array2Person, false),
        arguments(arrayBool, array2Nothing, false),
        arguments(arrayBool, array2A, false),

        arguments(arrayString, type, false),
        arguments(arrayString, bool, false),
        arguments(arrayString, string, false),
        arguments(arrayString, blob, false),
        arguments(arrayString, person, false),
        arguments(arrayString, nothing, true),
        arguments(arrayString, a, false),
        arguments(arrayString, arrayType, false),
        arguments(arrayString, arrayBool, false),
        arguments(arrayString, arrayString, true),
        arguments(arrayString, arrayBlob, false),
        arguments(arrayString, arrayPerson, true),
        arguments(arrayString, arrayNothing, true),
        arguments(arrayString, arrayA, false),
        arguments(arrayString, array2Type, false),
        arguments(arrayString, array2Bool, false),
        arguments(arrayString, array2String, false),
        arguments(arrayString, array2Blob, false),
        arguments(arrayString, array2Person, false),
        arguments(arrayString, array2Nothing, false),
        arguments(arrayString, array2A, false),

        arguments(arrayBlob, type, false),
        arguments(arrayBlob, bool, false),
        arguments(arrayBlob, string, false),
        arguments(arrayBlob, blob, false),
        arguments(arrayBlob, person, false),
        arguments(arrayBlob, nothing, true),
        arguments(arrayBlob, a, false),
        arguments(arrayBlob, arrayType, false),
        arguments(arrayBlob, arrayBool, false),
        arguments(arrayBlob, arrayString, false),
        arguments(arrayBlob, arrayBlob, true),
        arguments(arrayBlob, arrayPerson, false),
        arguments(arrayBlob, arrayNothing, true),
        arguments(arrayBlob, arrayA, false),
        arguments(arrayBlob, array2Type, false),
        arguments(arrayBlob, array2Bool, false),
        arguments(arrayBlob, array2String, false),
        arguments(arrayBlob, array2Blob, false),
        arguments(arrayBlob, array2Person, false),
        arguments(arrayBlob, array2Nothing, false),
        arguments(arrayBlob, array2A, false),

        arguments(arrayPerson, type, false),
        arguments(arrayPerson, bool, false),
        arguments(arrayPerson, string, false),
        arguments(arrayPerson, blob, false),
        arguments(arrayPerson, person, false),
        arguments(arrayPerson, nothing, true),
        arguments(arrayPerson, a, false),
        arguments(arrayPerson, arrayType, false),
        arguments(arrayPerson, arrayBool, false),
        arguments(arrayPerson, arrayString, false),
        arguments(arrayPerson, arrayBlob, false),
        arguments(arrayPerson, arrayPerson, true),
        arguments(arrayPerson, arrayNothing, true),
        arguments(arrayPerson, arrayA, false),
        arguments(arrayPerson, array2Type, false),
        arguments(arrayPerson, array2Bool, false),
        arguments(arrayPerson, array2String, false),
        arguments(arrayPerson, array2Blob, false),
        arguments(arrayPerson, array2Person, false),
        arguments(arrayPerson, array2Nothing, false),
        arguments(arrayPerson, array2A, false),

        arguments(arrayNothing, type, false),
        arguments(arrayNothing, bool, false),
        arguments(arrayNothing, string, false),
        arguments(arrayNothing, blob, false),
        arguments(arrayNothing, person, false),
        arguments(arrayNothing, nothing, true),
        arguments(arrayNothing, a, false),
        arguments(arrayNothing, arrayType, false),
        arguments(arrayNothing, arrayBool, false),
        arguments(arrayNothing, arrayString, false),
        arguments(arrayNothing, arrayBlob, false),
        arguments(arrayNothing, arrayPerson, false),
        arguments(arrayNothing, arrayNothing, true),
        arguments(arrayNothing, arrayA, false),
        arguments(arrayNothing, array2Type, false),
        arguments(arrayNothing, array2Bool, false),
        arguments(arrayNothing, array2String, false),
        arguments(arrayNothing, array2Blob, false),
        arguments(arrayNothing, array2Person, false),
        arguments(arrayNothing, array2Nothing, false),
        arguments(arrayNothing, array2A, false),

        arguments(arrayA, type, false),
        arguments(arrayA, bool, false),
        arguments(arrayA, string, false),
        arguments(arrayA, blob, false),
        arguments(arrayA, person, false),
        arguments(arrayA, nothing, true),
        arguments(arrayA, a, false),
        arguments(arrayA, b, false),
        arguments(arrayA, arrayType, true),
        arguments(arrayA, arrayBool, true),
        arguments(arrayA, arrayString, true),
        arguments(arrayA, arrayBlob, true),
        arguments(arrayA, arrayPerson, true),
        arguments(arrayA, arrayNothing, true),
        arguments(arrayA, arrayA, true),
        arguments(arrayA, arrayB, true),
        arguments(arrayA, array2Type, true),
        arguments(arrayA, array2Bool, true),
        arguments(arrayA, array2String, true),
        arguments(arrayA, array2Blob, true),
        arguments(arrayA, array2Person, true),
        arguments(arrayA, array2Nothing, true),
        arguments(arrayA, array2A, true),
        arguments(arrayA, array2B, true),

        arguments(array2Type, type, false),
        arguments(array2Type, bool, false),
        arguments(array2Type, string, false),
        arguments(array2Type, blob, false),
        arguments(array2Type, person, false),
        arguments(array2Type, nothing, true),
        arguments(array2Type, a, false),
        arguments(array2Type, arrayType, false),
        arguments(array2Type, arrayBool, false),
        arguments(array2Type, arrayString, false),
        arguments(array2Type, arrayBlob, false),
        arguments(array2Type, arrayPerson, false),
        arguments(array2Type, arrayNothing, true),
        arguments(array2Type, arrayA, false),
        arguments(array2Type, array2Type, true),
        arguments(array2Type, array2Bool, false),
        arguments(array2Type, array2String, false),
        arguments(array2Type, array2Blob, false),
        arguments(array2Type, array2Person, false),
        arguments(array2Type, array2Nothing, true),
        arguments(array2Type, array2A, false),

        arguments(array2Bool, type, false),
        arguments(array2Bool, bool, false),
        arguments(array2Bool, string, false),
        arguments(array2Bool, blob, false),
        arguments(array2Bool, person, false),
        arguments(array2Bool, nothing, true),
        arguments(array2Bool, a, false),
        arguments(array2Bool, arrayType, false),
        arguments(array2Bool, arrayBool, false),
        arguments(array2Bool, arrayString, false),
        arguments(array2Bool, arrayBlob, false),
        arguments(array2Bool, arrayPerson, false),
        arguments(array2Bool, arrayNothing, true),
        arguments(array2Bool, arrayA, false),
        arguments(array2Bool, array2Type, false),
        arguments(array2Bool, array2Bool, true),
        arguments(array2Bool, array2String, false),
        arguments(array2Bool, array2Blob, false),
        arguments(array2Bool, array2Person, false),
        arguments(array2Bool, array2Nothing, true),
        arguments(array2Bool, array2A, false),

        arguments(array2String, type, false),
        arguments(array2String, bool, false),
        arguments(array2String, string, false),
        arguments(array2String, blob, false),
        arguments(array2String, person, false),
        arguments(array2String, nothing, true),
        arguments(array2String, a, false),
        arguments(array2String, arrayType, false),
        arguments(array2String, arrayBool, false),
        arguments(array2String, arrayString, false),
        arguments(array2String, arrayBlob, false),
        arguments(array2String, arrayPerson, false),
        arguments(array2String, arrayNothing, true),
        arguments(array2String, arrayA, false),
        arguments(array2String, array2Type, false),
        arguments(array2String, array2Bool, false),
        arguments(array2String, array2String, true),
        arguments(array2String, array2Blob, false),
        arguments(array2String, array2Person, true),
        arguments(array2String, array2Nothing, true),
        arguments(array2String, array2A, false),

        arguments(array2Blob, type, false),
        arguments(array2Blob, bool, false),
        arguments(array2Blob, string, false),
        arguments(array2Blob, blob, false),
        arguments(array2Blob, person, false),
        arguments(array2Blob, nothing, true),
        arguments(array2Blob, a, false),
        arguments(array2Blob, arrayType, false),
        arguments(array2Blob, arrayBool, false),
        arguments(array2Blob, arrayString, false),
        arguments(array2Blob, arrayBlob, false),
        arguments(array2Blob, arrayPerson, false),
        arguments(array2Blob, arrayNothing, true),
        arguments(array2Blob, arrayA, false),
        arguments(array2Blob, array2Type, false),
        arguments(array2Blob, array2Bool, false),
        arguments(array2Blob, array2String, false),
        arguments(array2Blob, array2Blob, true),
        arguments(array2Blob, array2Person, false),
        arguments(array2Blob, array2Nothing, true),
        arguments(array2Blob, array2A, false),

        arguments(array2Person, type, false),
        arguments(array2Person, bool, false),
        arguments(array2Person, string, false),
        arguments(array2Person, blob, false),
        arguments(array2Person, person, false),
        arguments(array2Person, nothing, true),
        arguments(array2Person, a, false),
        arguments(array2Person, arrayType, false),
        arguments(array2Person, arrayBool, false),
        arguments(array2Person, arrayString, false),
        arguments(array2Person, arrayBlob, false),
        arguments(array2Person, arrayPerson, false),
        arguments(array2Person, arrayNothing, true),
        arguments(array2Person, arrayA, false),
        arguments(array2Person, array2Type, false),
        arguments(array2Person, array2Bool, false),
        arguments(array2Person, array2String, false),
        arguments(array2Person, array2Blob, false),
        arguments(array2Person, array2Person, true),
        arguments(array2Person, array2Nothing, true),
        arguments(array2Person, array2A, false),

        arguments(array2Nothing, type, false),
        arguments(array2Nothing, bool, false),
        arguments(array2Nothing, string, false),
        arguments(array2Nothing, blob, false),
        arguments(array2Nothing, person, false),
        arguments(array2Nothing, nothing, true),
        arguments(array2Nothing, a, false),
        arguments(array2Nothing, arrayType, false),
        arguments(array2Nothing, arrayBool, false),
        arguments(array2Nothing, arrayString, false),
        arguments(array2Nothing, arrayBlob, false),
        arguments(array2Nothing, arrayPerson, false),
        arguments(array2Nothing, arrayNothing, true),
        arguments(array2Nothing, arrayA, false),
        arguments(array2Nothing, array2Type, false),
        arguments(array2Nothing, array2Bool, false),
        arguments(array2Nothing, array2String, false),
        arguments(array2Nothing, array2Blob, false),
        arguments(array2Nothing, array2Person, false),
        arguments(array2Nothing, array2Nothing, true),
        arguments(array2Nothing, array2A, false),

        arguments(array2A, type, false),
        arguments(array2A, bool, false),
        arguments(array2A, string, false),
        arguments(array2A, blob, false),
        arguments(array2A, person, false),
        arguments(array2A, nothing, true),
        arguments(array2A, a, false),
        arguments(array2A, b, false),
        arguments(array2A, arrayType, false),
        arguments(array2A, arrayBool, false),
        arguments(array2A, arrayString, false),
        arguments(array2A, arrayBlob, false),
        arguments(array2A, arrayPerson, false),
        arguments(array2A, arrayNothing, true),
        arguments(array2A, arrayA, false),
        arguments(array2A, arrayB, false),
        arguments(array2A, array2Type, true),
        arguments(array2A, array2Bool, true),
        arguments(array2A, array2String, true),
        arguments(array2A, array2Blob, true),
        arguments(array2A, array2Person, true),
        arguments(array2A, array2Nothing, true),
        arguments(array2A, array2A, true),
        arguments(array2A, array2B, true));
  }

  @ParameterizedTest
  @MethodSource("commonSuperType_test_data")
  public void commonSuperType(Type type1, Type type2, Optional<Type> expected) {
    assertThat(type1.commonSuperType(type2))
        .isEqualTo(expected);
    assertThat(type2.commonSuperType(type1))
        .isEqualTo(expected);
  }

  public static List<Arguments> commonSuperType_test_data() {
    return List.of(
        arguments(type, type, Optional.of(type)),
        arguments(type, bool, Optional.empty()),
        arguments(type, string, Optional.empty()),
        arguments(type, blob, Optional.empty()),
        arguments(type, nothing, Optional.of(type)),
        arguments(type, a, Optional.empty()),
        arguments(type, arrayType, Optional.empty()),
        arguments(type, arrayBool, Optional.empty()),
        arguments(type, arrayString, Optional.empty()),
        arguments(type, arrayBlob, Optional.empty()),
        arguments(type, arrayNothing, Optional.empty()),
        arguments(type, arrayA, Optional.empty()),

        arguments(bool, string, Optional.empty()),
        arguments(bool, bool, Optional.of(bool)),
        arguments(bool, blob, Optional.empty()),
        arguments(bool, nothing, Optional.of(bool)),
        arguments(bool, a, Optional.empty()),
        arguments(bool, arrayString, Optional.empty()),
        arguments(bool, arrayBool, Optional.empty()),
        arguments(bool, arrayType, Optional.empty()),
        arguments(bool, arrayBlob, Optional.empty()),
        arguments(bool, arrayNothing, Optional.empty()),
        arguments(bool, arrayA, Optional.empty()),

        arguments(string, string, Optional.of(string)),
        arguments(string, blob, Optional.empty()),
        arguments(string, nothing, Optional.of(string)),
        arguments(string, a, Optional.empty()),
        arguments(string, arrayString, Optional.empty()),
        arguments(string, arrayBool, Optional.empty()),
        arguments(string, arrayType, Optional.empty()),
        arguments(string, arrayBlob, Optional.empty()),
        arguments(string, arrayNothing, Optional.empty()),
        arguments(string, arrayA, Optional.empty()),

        arguments(blob, blob, Optional.of(blob)),
        arguments(blob, nothing, Optional.of(blob)),
        arguments(blob, a, Optional.empty()),
        arguments(blob, arrayType, Optional.empty()),
        arguments(blob, arrayString, Optional.empty()),
        arguments(blob, arrayBlob, Optional.empty()),
        arguments(blob, arrayNothing, Optional.empty()),
        arguments(blob, arrayA, Optional.empty()),

        arguments(nothing, nothing, Optional.of(nothing)),
        arguments(nothing, a, Optional.of(a)),
        arguments(nothing, arrayType, Optional.of(arrayType)),
        arguments(nothing, arrayString, Optional.of(arrayString)),
        arguments(nothing, arrayBlob, Optional.of(arrayBlob)),
        arguments(nothing, arrayNothing, Optional.of(arrayNothing)),
        arguments(nothing, arrayA, Optional.of(arrayA)),

        arguments(a, a, Optional.of(a)),
        arguments(a, b, Optional.empty()),
        arguments(a, arrayType, Optional.empty()),
        arguments(a, arrayString, Optional.empty()),
        arguments(a, arrayBlob, Optional.empty()),
        arguments(a, arrayNothing, Optional.empty()),
        arguments(a, arrayA, Optional.empty()),
        arguments(a, arrayB, Optional.empty()),

        arguments(arrayType, arrayType, Optional.of(arrayType)),
        arguments(arrayType, arrayString, Optional.empty()),
        arguments(arrayType, arrayBlob, Optional.empty()),
        arguments(arrayType, arrayNothing, Optional.of(arrayType)),
        arguments(arrayType, arrayA, Optional.empty()),

        arguments(arrayString, arrayString, Optional.of(arrayString)),
        arguments(arrayString, arrayBlob, Optional.empty()),
        arguments(arrayString, arrayNothing, Optional.of(arrayString)),
        arguments(arrayString, nothing, Optional.of(arrayString)),
        arguments(arrayString, arrayA, Optional.empty()),

        arguments(arrayBlob, arrayBlob, Optional.of(arrayBlob)),
        arguments(arrayBlob, arrayNothing, Optional.of(arrayBlob)),
        arguments(arrayBlob, nothing, Optional.of(arrayBlob)),
        arguments(arrayBlob, arrayA, Optional.empty()),

        arguments(arrayNothing, arrayNothing, Optional.of(arrayNothing)),
        arguments(arrayNothing, array2Nothing, Optional.of(array2Nothing)),
        arguments(arrayNothing, arrayType, Optional.of(arrayType)),
        arguments(arrayNothing, arrayString, Optional.of(arrayString)),
        arguments(arrayNothing, arrayBlob, Optional.of(arrayBlob)),
        arguments(arrayNothing, arrayA, Optional.of(arrayA)),

        arguments(arrayA, arrayA, Optional.of(arrayA)),
        arguments(arrayA, arrayB, Optional.empty()));
  }

  @ParameterizedTest
  @MethodSource("actualCoreTypeWhenAssignedFrom_test_data")
  public void actualCoreTypeWhenAssignedFrom(GenericType type, Type assigned, Type expected) {
    if (expected == null) {
      assertCall(() -> type.actualCoreTypeWhenAssignedFrom(assigned))
          .throwsException(IllegalArgumentException.class);
    } else {
      assertThat(type.actualCoreTypeWhenAssignedFrom(assigned))
          .isEqualTo(expected);
    }
  }

  public static List<Arguments> actualCoreTypeWhenAssignedFrom_test_data() {
    return List.of(
        arguments(a, type, type),
        arguments(a, bool, bool),
        arguments(a, string, string),
        arguments(a, blob, blob),
        arguments(a, person, person),
        arguments(a, nothing, nothing),
        arguments(a, a, a),
        arguments(a, b, b),

        arguments(a, arrayType, arrayType),
        arguments(a, arrayBool, arrayBool),
        arguments(a, arrayString, arrayString),
        arguments(a, arrayBlob, arrayBlob),
        arguments(a, arrayPerson, arrayPerson),
        arguments(a, arrayNothing, arrayNothing),
        arguments(a, arrayA, arrayA),
        arguments(a, arrayB, arrayB),

        arguments(a, array2Type, array2Type),
        arguments(a, array2Bool, array2Bool),
        arguments(a, array2String, array2String),
        arguments(a, array2Blob, array2Blob),
        arguments(a, array2Person, array2Person),
        arguments(a, array2Nothing, array2Nothing),
        arguments(a, array2A, array2A),
        arguments(a, array2B, array2B),

        arguments(arrayA, type, null),
        arguments(arrayA, bool, null),
        arguments(arrayA, string, null),
        arguments(arrayA, blob, null),
        arguments(arrayA, person, null),
        arguments(arrayA, nothing, nothing),
        arguments(arrayA, a, null),
        arguments(arrayA, b, null),

        arguments(arrayA, arrayType, type),
        arguments(arrayA, arrayBool, bool),
        arguments(arrayA, arrayString, string),
        arguments(arrayA, arrayBlob, blob),
        arguments(arrayA, arrayPerson, person),
        arguments(arrayA, arrayNothing, nothing),
        arguments(arrayA, arrayA, a),
        arguments(arrayA, arrayB, b),

        arguments(arrayA, array2Type, arrayType),
        arguments(arrayA, array2Bool, arrayBool),
        arguments(arrayA, array2String, arrayString),
        arguments(arrayA, array2Blob, arrayBlob),
        arguments(arrayA, array2Person, arrayPerson),
        arguments(arrayA, array2Nothing, arrayNothing),
        arguments(arrayA, array2A, arrayA),
        arguments(arrayA, array2B, arrayB),

        arguments(array2A, type, null),
        arguments(array2A, bool, null),
        arguments(array2A, string, null),
        arguments(array2A, blob, null),
        arguments(array2A, person, null),
        arguments(array2A, nothing, nothing),
        arguments(array2A, a, null),
        arguments(array2A, b, null),

        arguments(array2A, arrayType, null),
        arguments(array2A, arrayBool, null),
        arguments(array2A, arrayString, null),
        arguments(array2A, arrayBlob, null),
        arguments(array2A, arrayPerson, null),
        arguments(array2A, arrayNothing, nothing),
        arguments(array2A, arrayA, null),
        arguments(array2A, arrayB, null),

        arguments(array2A, array2Type, type),
        arguments(array2A, array2Bool, bool),
        arguments(array2A, array2String, string),
        arguments(array2A, array2Blob, blob),
        arguments(array2A, array2Person, person),
        arguments(array2A, array2Nothing, nothing),
        arguments(array2A, array2A, a),
        arguments(array2A, array2B, b));
  }

  @ParameterizedTest
  @MethodSource("elemType_test_data")
  public void elemType(ArrayType type, Type expected) {
    assertThat(type.elemType())
        .isEqualTo(expected);
  }

  public static List<Arguments> elemType_test_data() {
    return List.of(
        arguments(arrayType, type),
        arguments(arrayBool, bool),
        arguments(arrayString, string),
        arguments(arrayBlob, blob),
        arguments(arrayPerson, person),
        arguments(arrayNothing, nothing),

        arguments(array2Type, arrayType),
        arguments(array2Bool, arrayBool),
        arguments(array2String, arrayString),
        arguments(array2Blob, arrayBlob),
        arguments(array2Person, arrayPerson),
        arguments(array2Nothing, arrayNothing));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(type, type);
    tester.addEqualityGroup(bool, bool);
    tester.addEqualityGroup(string, string);
    tester.addEqualityGroup(blob, blob);
    tester.addEqualityGroup(nothing, nothing);
    tester.addEqualityGroup(person, person);
    tester.addEqualityGroup(a, a);

    tester.addEqualityGroup(arrayType, arrayType);
    tester.addEqualityGroup(arrayBool, arrayBool);
    tester.addEqualityGroup(arrayString, arrayString);
    tester.addEqualityGroup(arrayBlob, arrayBlob);
    tester.addEqualityGroup(arrayPerson, arrayPerson);
    tester.addEqualityGroup(arrayA, arrayA);

    tester.addEqualityGroup(array2Type, array2Type);
    tester.addEqualityGroup(array2Bool, array2Bool);
    tester.addEqualityGroup(array2String, array2String);
    tester.addEqualityGroup(array2Blob, array2Blob);
    tester.addEqualityGroup(array2Person, array2Person);
    tester.addEqualityGroup(array2A, array2A);
    tester.testEquals();
  }
}
