package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Blob;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Bool;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Nothing;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Person;
import static org.smoothbuild.lang.object.type.TestingTypes.array2String;
import static org.smoothbuild.lang.object.type.TestingTypes.array2Type;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayBlob;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayBool;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayNothing;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayPerson;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayString;
import static org.smoothbuild.lang.object.type.TestingTypes.arrayType;
import static org.smoothbuild.lang.object.type.TestingTypes.blob;
import static org.smoothbuild.lang.object.type.TestingTypes.bool;
import static org.smoothbuild.lang.object.type.TestingTypes.nothing;
import static org.smoothbuild.lang.object.type.TestingTypes.person;
import static org.smoothbuild.lang.object.type.TestingTypes.string;
import static org.smoothbuild.lang.object.type.TestingTypes.type;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.Nothing;
import org.smoothbuild.lang.object.base.SString;

import com.google.common.testing.EqualsTester;

public class TypeTest {
  @ParameterizedTest
  @MethodSource("names")
  public void name(BinaryType type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(BinaryType type, String name) {
    assertThat(type.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(BinaryType type, String name) {
    assertThat(type.toString())
        .isEqualTo(name + ":" + type.hash());
  }

  public static Stream<Arguments> names() {
    return Stream.of(
        arguments(type, "TYPE"),
        arguments(bool, "BOOL"),
        arguments(string, "STRING"),
        arguments(blob, "BLOB"),
        arguments(nothing, "NOTHING"),
        arguments(person, "TUPLE"),

        arguments(arrayType, "[TYPE]"),
        arguments(arrayBool, "[BOOL]"),
        arguments(arrayString, "[STRING]"),
        arguments(arrayBlob, "[BLOB]"),
        arguments(arrayNothing, "[NOTHING]"),
        arguments(arrayPerson, "[TUPLE]"),

        arguments(array2Type, "[[TYPE]]"),
        arguments(array2Bool, "[[BOOL]]"),
        arguments(array2String, "[[STRING]]"),
        arguments(array2Blob, "[[BLOB]]"),
        arguments(array2Nothing, "[[NOTHING]]"),
        arguments(array2Person, "[[TUPLE]]")
    );
  }

  @ParameterizedTest
  @MethodSource("jType_test_data")
  public void jType(BinaryType type, Class<?> expected) {
    assertThat(type.jType())
        .isEqualTo(expected);
  }

  public static List<Arguments> jType_test_data() {
    return List.of(
        arguments(type, BinaryType.class),
        arguments(bool, Bool.class),
        arguments(string, SString.class),
        arguments(blob, Blob.class),
        arguments(nothing, Nothing.class),
        arguments(arrayType, Array.class),
        arguments(arrayBool, Array.class),
        arguments(arrayString, Array.class),
        arguments(arrayBlob, Array.class),
        arguments(arrayNothing, Array.class)
    );
  }

  @ParameterizedTest
  @MethodSource("isArray_test_data")
  public void isArray(BinaryType type, boolean expected) {
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

        arguments(arrayType, true),
        arguments(arrayString, true),
        arguments(arrayBool, true),
        arguments(arrayBlob, true),
        arguments(arrayNothing, true),
        arguments(arrayPerson, true),
        arguments(array2Type, true),
        arguments(array2Bool, true),
        arguments(array2String, true),
        arguments(array2Blob, true),
        arguments(array2Nothing, true),
        arguments(array2Person, true)
    );
  }

  @ParameterizedTest
  @MethodSource("elemType_test_data")
  public void elemType(ArrayType type, BinaryType expected) {
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

    tester.addEqualityGroup(arrayType, arrayType);
    tester.addEqualityGroup(arrayBool, arrayBool);
    tester.addEqualityGroup(arrayString, arrayString);
    tester.addEqualityGroup(arrayBlob, arrayBlob);
    tester.addEqualityGroup(arrayPerson, arrayPerson);

    tester.addEqualityGroup(array2Type, array2Type);
    tester.addEqualityGroup(array2Bool, array2Bool);
    tester.addEqualityGroup(array2String, array2String);
    tester.addEqualityGroup(array2Blob, array2Blob);
    tester.addEqualityGroup(array2Person, array2Person);
    tester.testEquals();
  }
}
