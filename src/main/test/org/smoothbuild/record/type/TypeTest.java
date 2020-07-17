package org.smoothbuild.record.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.record.type.TestingTypes.ARRAY2_BLOB;
import static org.smoothbuild.record.type.TestingTypes.ARRAY2_BOOL;
import static org.smoothbuild.record.type.TestingTypes.ARRAY2_NOTHING;
import static org.smoothbuild.record.type.TestingTypes.ARRAY2_PERSON;
import static org.smoothbuild.record.type.TestingTypes.ARRAY2_STRING;
import static org.smoothbuild.record.type.TestingTypes.ARRAY2_TYPE;
import static org.smoothbuild.record.type.TestingTypes.ARRAY_BLOB;
import static org.smoothbuild.record.type.TestingTypes.ARRAY_BOOL;
import static org.smoothbuild.record.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.record.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.record.type.TestingTypes.ARRAY_STRING;
import static org.smoothbuild.record.type.TestingTypes.ARRAY_TYPE;
import static org.smoothbuild.record.type.TestingTypes.BLOB;
import static org.smoothbuild.record.type.TestingTypes.BOOL;
import static org.smoothbuild.record.type.TestingTypes.NOTHING;
import static org.smoothbuild.record.type.TestingTypes.PERSON;
import static org.smoothbuild.record.type.TestingTypes.STRING;
import static org.smoothbuild.record.type.TestingTypes.TYPE;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Blob;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.Nothing;
import org.smoothbuild.record.base.SString;

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
        arguments(TYPE, "TYPE"),
        arguments(BOOL, "BOOL"),
        arguments(STRING, "STRING"),
        arguments(BLOB, "BLOB"),
        arguments(NOTHING, "NOTHING"),
        arguments(PERSON, "TUPLE"),

        arguments(ARRAY_TYPE, "[TYPE]"),
        arguments(ARRAY_BOOL, "[BOOL]"),
        arguments(ARRAY_STRING, "[STRING]"),
        arguments(ARRAY_BLOB, "[BLOB]"),
        arguments(ARRAY_NOTHING, "[NOTHING]"),
        arguments(ARRAY_PERSON, "[TUPLE]"),

        arguments(ARRAY2_TYPE, "[[TYPE]]"),
        arguments(ARRAY2_BOOL, "[[BOOL]]"),
        arguments(ARRAY2_STRING, "[[STRING]]"),
        arguments(ARRAY2_BLOB, "[[BLOB]]"),
        arguments(ARRAY2_NOTHING, "[[NOTHING]]"),
        arguments(ARRAY2_PERSON, "[[TUPLE]]")
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
        arguments(TYPE, BinaryType.class),
        arguments(BOOL, Bool.class),
        arguments(STRING, SString.class),
        arguments(BLOB, Blob.class),
        arguments(NOTHING, Nothing.class),
        arguments(ARRAY_TYPE, Array.class),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_STRING, Array.class),
        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_NOTHING, Array.class)
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
        arguments(TYPE, false),
        arguments(BOOL, false),
        arguments(STRING, false),
        arguments(BLOB, false),
        arguments(NOTHING, false),
        arguments(PERSON, false),

        arguments(ARRAY_TYPE, true),
        arguments(ARRAY_STRING, true),
        arguments(ARRAY_BOOL, true),
        arguments(ARRAY_BLOB, true),
        arguments(ARRAY_NOTHING, true),
        arguments(ARRAY_PERSON, true),
        arguments(ARRAY2_TYPE, true),
        arguments(ARRAY2_BOOL, true),
        arguments(ARRAY2_STRING, true),
        arguments(ARRAY2_BLOB, true),
        arguments(ARRAY2_NOTHING, true),
        arguments(ARRAY2_PERSON, true)
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
        arguments(ARRAY_TYPE, TYPE),
        arguments(ARRAY_BOOL, BOOL),
        arguments(ARRAY_STRING, STRING),
        arguments(ARRAY_BLOB, BLOB),
        arguments(ARRAY_PERSON, PERSON),
        arguments(ARRAY_NOTHING, NOTHING),

        arguments(ARRAY2_TYPE, ARRAY_TYPE),
        arguments(ARRAY2_BOOL, ARRAY_BOOL),
        arguments(ARRAY2_STRING, ARRAY_STRING),
        arguments(ARRAY2_BLOB, ARRAY_BLOB),
        arguments(ARRAY2_PERSON, ARRAY_PERSON),
        arguments(ARRAY2_NOTHING, ARRAY_NOTHING));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(TYPE, TYPE);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(STRING, STRING);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(PERSON, PERSON);

    tester.addEqualityGroup(ARRAY_TYPE, ARRAY_TYPE);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_STRING, ARRAY_STRING);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_PERSON, ARRAY_PERSON);

    tester.addEqualityGroup(ARRAY2_TYPE, ARRAY2_TYPE);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_STRING, ARRAY2_STRING);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_PERSON, ARRAY2_PERSON);
    tester.testEquals();
  }
}
