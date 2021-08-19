package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY2_STRING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.ARRAY_STRING;
import static org.smoothbuild.db.object.spec.TestingSpecs.BLOB;
import static org.smoothbuild.db.object.spec.TestingSpecs.BOOL;
import static org.smoothbuild.db.object.spec.TestingSpecs.INT;
import static org.smoothbuild.db.object.spec.TestingSpecs.NOTHING;
import static org.smoothbuild.db.object.spec.TestingSpecs.PERSON;
import static org.smoothbuild.db.object.spec.TestingSpecs.STRING;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Int;
import org.smoothbuild.db.object.base.Str;

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
        arguments(INT, "INT"),
        arguments(BOOL, "BOOL"),
        arguments(STRING, "STRING"),
        arguments(BLOB, "BLOB"),
        arguments(NOTHING, "NOTHING"),
        arguments(PERSON, "{STRING,STRING}"),

        arguments(ARRAY_INT, "[INT]"),
        arguments(ARRAY_BOOL, "[BOOL]"),
        arguments(ARRAY_STRING, "[STRING]"),
        arguments(ARRAY_BLOB, "[BLOB]"),
        arguments(ARRAY_NOTHING, "[NOTHING]"),
        arguments(ARRAY_PERSON, "[{STRING,STRING}]"),

        arguments(ARRAY2_INT, "[[INT]]"),
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
    return list(
        arguments(INT, Int.class),
        arguments(BOOL, Bool.class),
        arguments(STRING, Str.class),
        arguments(BLOB, Blob.class),
        arguments(NOTHING, null),
        arguments(ARRAY_BOOL, Array.class),
        arguments(ARRAY_STRING, Array.class),
        arguments(ARRAY_BLOB, Array.class),
        arguments(ARRAY_NOTHING, Array.class)
    );
  }

  @ParameterizedTest
  @MethodSource("elem_spec_test_data")
  public void elemSpec(ArraySpec spec, Spec expected) {
    assertThat(spec.elemSpec())
        .isEqualTo(expected);
  }

  public static List<Arguments> elem_spec_test_data() {
    return list(
        arguments(ARRAY_INT, INT),
        arguments(ARRAY_BOOL, BOOL),
        arguments(ARRAY_STRING, STRING),
        arguments(ARRAY_BLOB, BLOB),
        arguments(ARRAY_PERSON, PERSON),
        arguments(ARRAY_NOTHING, NOTHING),

        arguments(ARRAY2_INT, ARRAY_INT),
        arguments(ARRAY2_BOOL, ARRAY_BOOL),
        arguments(ARRAY2_STRING, ARRAY_STRING),
        arguments(ARRAY2_BLOB, ARRAY_BLOB),
        arguments(ARRAY2_PERSON, ARRAY_PERSON),
        arguments(ARRAY2_NOTHING, ARRAY_NOTHING));
  }

  @Test
  public void equals_and_hashcode() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(INT, INT);
    tester.addEqualityGroup(BOOL, BOOL);
    tester.addEqualityGroup(STRING, STRING);
    tester.addEqualityGroup(BLOB, BLOB);
    tester.addEqualityGroup(NOTHING, NOTHING);
    tester.addEqualityGroup(PERSON, PERSON);

    tester.addEqualityGroup(ARRAY_INT, ARRAY_INT);
    tester.addEqualityGroup(ARRAY_BOOL, ARRAY_BOOL);
    tester.addEqualityGroup(ARRAY_STRING, ARRAY_STRING);
    tester.addEqualityGroup(ARRAY_BLOB, ARRAY_BLOB);
    tester.addEqualityGroup(ARRAY_PERSON, ARRAY_PERSON);

    tester.addEqualityGroup(ARRAY2_INT, ARRAY2_INT);
    tester.addEqualityGroup(ARRAY2_BOOL, ARRAY2_BOOL);
    tester.addEqualityGroup(ARRAY2_STRING, ARRAY2_STRING);
    tester.addEqualityGroup(ARRAY2_BLOB, ARRAY2_BLOB);
    tester.addEqualityGroup(ARRAY2_PERSON, ARRAY2_PERSON);
    tester.testEquals();
  }
}
