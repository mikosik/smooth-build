package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_STRING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_STRING;
import static org.smoothbuild.db.object.spec.TestingSpecs.BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.STRING;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.RString;

import com.google.common.testing.EqualsTester;

public class SpecTest {
  @ParameterizedTest
  @MethodSource("names")
  public void name(Spec spec, String name) {
    assertThat(spec.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void quoted_name(Spec spec, String name) {
    assertThat(spec.name())
        .isEqualTo(name);
  }

  @ParameterizedTest
  @MethodSource("names")
  public void to_string(Spec spec, String name) {
    assertThat(spec.toString())
        .isEqualTo(name + ":" + spec.hash());
  }

  public static Stream<Arguments> names() {
    return Stream.of(
        arguments(BOOL, "BOOL"),
        arguments(STRING, "STRING"),
        arguments(BLOB, "BLOB"),
        arguments(NOTHING, "NOTHING"),
        arguments(PERSON, "{STRING,STRING}"),

        arguments(ARRAY_BOOL, "[BOOL]"),
        arguments(ARRAY_STRING, "[STRING]"),
        arguments(ARRAY_BLOB, "[BLOB]"),
        arguments(ARRAY_NOTHING, "[NOTHING]"),
        arguments(ARRAY_PERSON, "[{STRING,STRING}]"),

        arguments(ARRAY2_BOOL, "[[BOOL]]"),
        arguments(ARRAY2_STRING, "[[STRING]]"),
        arguments(ARRAY2_BLOB, "[[BLOB]]"),
        arguments(ARRAY2_NOTHING, "[[NOTHING]]"),
        arguments(ARRAY2_PERSON, "[[{STRING,STRING}]]")
    );
  }

  @ParameterizedTest
  @MethodSource("jType_test_data")
  public void jType(Spec spec, Class<?> expected) {
    assertThat(spec.jType())
        .isEqualTo(expected);
  }

  public static List<Arguments> jType_test_data() {
    return List.of(
        arguments(BOOL, Bool.class),
        arguments(STRING, RString.class),
        arguments(BLOB, Blob.class),
        arguments(NOTHING, Obj.class),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_STRING, Array.class),
        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_NOTHING, Array.class)
    );
  }

  @ParameterizedTest
  @MethodSource("isArray_test_data")
  public void isArray(Spec spec, boolean expected) {
    assertThat(spec.isArray())
        .isEqualTo(expected);
  }

  public static List<Arguments> isArray_test_data() {
    return List.of(
        arguments(BOOL, false),
        arguments(STRING, false),
        arguments(BLOB, false),
        arguments(NOTHING, false),
        arguments(PERSON, false),

        arguments(ARRAY_STRING, true),
        arguments(ARRAY_BOOL, true),
        arguments(ARRAY_BLOB, true),
        arguments(ARRAY_NOTHING, true),
        arguments(ARRAY_PERSON, true),
        arguments(ARRAY2_BOOL, true),
        arguments(ARRAY2_STRING, true),
        arguments(ARRAY2_BLOB, true),
        arguments(ARRAY2_NOTHING, true),
        arguments(ARRAY2_PERSON, true)
    );
  }

  @ParameterizedTest
  @MethodSource("elem_spec_test_data")
  public void elemSpec(ArraySpec spec, Spec expected) {
    assertThat(spec.elemSpec())
        .isEqualTo(expected);
  }

  public static List<Arguments> elem_spec_test_data() {
    return List.of(
        arguments(ARRAY_BOOL, BOOL),
        arguments(ARRAY_STRING, STRING),
        arguments(ARRAY_BLOB, BLOB),
        arguments(ARRAY_PERSON, PERSON),
        arguments(ARRAY_NOTHING, NOTHING),

        arguments(ARRAY2_BOOL, ARRAY_BOOL),
        arguments(ARRAY2_STRING, ARRAY_STRING),
        arguments(ARRAY2_BLOB, ARRAY_BLOB),
        arguments(ARRAY2_PERSON, ARRAY_PERSON),
        arguments(ARRAY2_NOTHING, ARRAY_NOTHING));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(STRING, STRING);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(PERSON, PERSON);

    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_STRING, ARRAY_STRING);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_PERSON, ARRAY_PERSON);

    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_STRING, ARRAY2_STRING);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_PERSON, ARRAY2_PERSON);
    tester.testEquals();
  }
}
