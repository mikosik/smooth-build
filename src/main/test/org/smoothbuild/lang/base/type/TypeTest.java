package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.BOOL;
import static org.smoothbuild.lang.base.type.TestingTypes.FAKE_LOCATION;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.Types.BASIC_TYPES;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.generic;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.base.Location;

import com.google.common.testing.EqualsTester;

public class TypeTest {
  private static final Location LOCATION = internal();

  @Test
  public void verify_all_basic_types_are_tested() {
    assertThat(BASIC_TYPES)
        .hasSize(4);
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
        .isEqualTo("Type(\"" + name + "\")");
  }

  public static Stream<Arguments> names() {
    return Stream.of(
        arguments(BOOL, "Bool"),
        arguments(STRING, "String"),
        arguments(BLOB, "Blob"),
        arguments(NOTHING, "Nothing"),
        arguments(PERSON, "Person"),
        arguments(A, "A"),

        arguments(ARRAY_BOOL, "[Bool]"),
        arguments(ARRAY_STRING, "[String]"),
        arguments(ARRAY_BLOB, "[Blob]"),
        arguments(ARRAY_NOTHING, "[Nothing]"),
        arguments(ARRAY_PERSON, "[Person]"),
        arguments(ARRAY_A, "[A]"),

        arguments(ARRAY2_BOOL, "[[Bool]]"),
        arguments(ARRAY2_STRING, "[[String]]"),
        arguments(ARRAY2_BLOB, "[[Blob]]"),
        arguments(ARRAY2_NOTHING, "[[Nothing]]"),
        arguments(ARRAY2_PERSON, "[[Person]]"),
        arguments(ARRAY2_A, "[[A]]")
    );
  }


  @ParameterizedTest
  @MethodSource("coreType_test_data")
  public void coreType(Type type, Type expected) {
    assertThat(type.coreType())
        .isEqualTo(expected);
  }

  public static List<Arguments> coreType_test_data() {
    return List.of(
        arguments(BOOL, BOOL),
        arguments(STRING, STRING),
        arguments(BLOB, BLOB),
        arguments(NOTHING, NOTHING),
        arguments(PERSON, PERSON),
        arguments(A, A),

        arguments(ARRAY_BOOL, BOOL),
        arguments(ARRAY_STRING, STRING),
        arguments(ARRAY_BLOB, BLOB),
        arguments(ARRAY_NOTHING, NOTHING),
        arguments(ARRAY_PERSON, PERSON),
        arguments(ARRAY_A, A),

        arguments(ARRAY2_BOOL, BOOL),
        arguments(ARRAY2_STRING, STRING),
        arguments(ARRAY2_BLOB, BLOB),
        arguments(ARRAY2_NOTHING, NOTHING),
        arguments(ARRAY2_PERSON, PERSON),
        arguments(ARRAY2_A, A)
    );
  }

  @ParameterizedTest
  @MethodSource("replaceCoreType_test_data")
  public void replaceCoreType(Type type, Type coreType, Type expected) {
    assertThat(type.replaceCoreType(coreType))
        .isEqualTo(expected);
  }

  public static List<Arguments> replaceCoreType_test_data() {
    return List.of(
        arguments(BOOL, BOOL, BOOL),
        arguments(BOOL, STRING, STRING),
        arguments(BOOL, BLOB, BLOB),
        arguments(BOOL, NOTHING, NOTHING),
        arguments(BOOL, PERSON, PERSON),
        arguments(BOOL, A, A),

        arguments(STRING, BOOL, BOOL),
        arguments(STRING, STRING, STRING),
        arguments(STRING, BLOB, BLOB),
        arguments(STRING, NOTHING, NOTHING),
        arguments(STRING, PERSON, PERSON),
        arguments(STRING, A, A),

        arguments(BLOB, BOOL, BOOL),
        arguments(BLOB, STRING, STRING),
        arguments(BLOB, BLOB, BLOB),
        arguments(BLOB, NOTHING, NOTHING),
        arguments(BLOB, PERSON, PERSON),
        arguments(BLOB, A, A),

        arguments(NOTHING, BOOL, BOOL),
        arguments(NOTHING, STRING, STRING),
        arguments(NOTHING, BLOB, BLOB),
        arguments(NOTHING, NOTHING, NOTHING),
        arguments(NOTHING, PERSON, PERSON),
        arguments(NOTHING, A, A),

        arguments(PERSON, BOOL, BOOL),
        arguments(PERSON, STRING, STRING),
        arguments(PERSON, BLOB, BLOB),
        arguments(PERSON, NOTHING, NOTHING),
        arguments(PERSON, PERSON, PERSON),
        arguments(PERSON, A, A),

        //

        arguments(BOOL, ARRAY_BOOL, ARRAY_BOOL),
        arguments(BOOL, ARRAY_STRING, ARRAY_STRING),
        arguments(BOOL, ARRAY_BLOB, ARRAY_BLOB),
        arguments(BOOL, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(BOOL, ARRAY_PERSON, ARRAY_PERSON),
        arguments(BOOL, ARRAY_A, ARRAY_A),

        arguments(STRING, ARRAY_BOOL, ARRAY_BOOL),
        arguments(STRING, ARRAY_STRING, ARRAY_STRING),
        arguments(STRING, ARRAY_BLOB, ARRAY_BLOB),
        arguments(STRING, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(STRING, ARRAY_PERSON, ARRAY_PERSON),
        arguments(STRING, ARRAY_A, ARRAY_A),

        arguments(BLOB, ARRAY_BOOL, ARRAY_BOOL),
        arguments(BLOB, ARRAY_STRING, ARRAY_STRING),
        arguments(BLOB, ARRAY_BLOB, ARRAY_BLOB),
        arguments(BLOB, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(BLOB, ARRAY_PERSON, ARRAY_PERSON),
        arguments(BLOB, ARRAY_A, ARRAY_A),

        arguments(NOTHING, ARRAY_BOOL, ARRAY_BOOL),
        arguments(NOTHING, ARRAY_STRING, ARRAY_STRING),
        arguments(NOTHING, ARRAY_BLOB, ARRAY_BLOB),
        arguments(NOTHING, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(NOTHING, ARRAY_PERSON, ARRAY_PERSON),
        arguments(NOTHING, ARRAY_A, ARRAY_A),

        arguments(PERSON, ARRAY_BOOL, ARRAY_BOOL),
        arguments(PERSON, ARRAY_STRING, ARRAY_STRING),
        arguments(PERSON, ARRAY_BLOB, ARRAY_BLOB),
        arguments(PERSON, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(PERSON, ARRAY_PERSON, ARRAY_PERSON),
        arguments(PERSON, ARRAY_A, ARRAY_A),

        arguments(A, ARRAY_BOOL, ARRAY_BOOL),
        arguments(A, ARRAY_STRING, ARRAY_STRING),
        arguments(A, ARRAY_BLOB, ARRAY_BLOB),
        arguments(A, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(A, ARRAY_PERSON, ARRAY_PERSON),
        arguments(A, ARRAY_A, ARRAY_A),

        //

        arguments(ARRAY_BOOL, BOOL, ARRAY_BOOL),
        arguments(ARRAY_BOOL, STRING, ARRAY_STRING),
        arguments(ARRAY_BOOL, BLOB, ARRAY_BLOB),
        arguments(ARRAY_BOOL, NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_BOOL, PERSON, ARRAY_PERSON),
        arguments(ARRAY_BOOL, A, ARRAY_A),

        arguments(ARRAY_STRING, BOOL, ARRAY_BOOL),
        arguments(ARRAY_STRING, STRING, ARRAY_STRING),
        arguments(ARRAY_STRING, BLOB, ARRAY_BLOB),
        arguments(ARRAY_STRING, NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_STRING, PERSON, ARRAY_PERSON),
        arguments(ARRAY_STRING, A, ARRAY_A),

        arguments(ARRAY_BLOB, BOOL, ARRAY_BOOL),
        arguments(ARRAY_BLOB, STRING, ARRAY_STRING),
        arguments(ARRAY_BLOB, BLOB, ARRAY_BLOB),
        arguments(ARRAY_BLOB, NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_BLOB, PERSON, ARRAY_PERSON),
        arguments(ARRAY_BLOB, A, ARRAY_A),

        arguments(ARRAY_NOTHING, BOOL, ARRAY_BOOL),
        arguments(ARRAY_NOTHING, STRING, ARRAY_STRING),
        arguments(ARRAY_NOTHING, BLOB, ARRAY_BLOB),
        arguments(ARRAY_NOTHING, NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_NOTHING, PERSON, ARRAY_PERSON),
        arguments(ARRAY_NOTHING, A, ARRAY_A),

        arguments(ARRAY_PERSON, BOOL, ARRAY_BOOL),
        arguments(ARRAY_PERSON, STRING, ARRAY_STRING),
        arguments(ARRAY_PERSON, BLOB, ARRAY_BLOB),
        arguments(ARRAY_PERSON, NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_PERSON, PERSON, ARRAY_PERSON),
        arguments(ARRAY_PERSON, A, ARRAY_A),

        arguments(ARRAY_A, BOOL, ARRAY_BOOL),
        arguments(ARRAY_A, STRING, ARRAY_STRING),
        arguments(ARRAY_A, BLOB, ARRAY_BLOB),
        arguments(ARRAY_A, NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_A, PERSON, ARRAY_PERSON),
        arguments(ARRAY_A, A, ARRAY_A),

        //

        arguments(ARRAY_BOOL, ARRAY_BOOL, ARRAY2_BOOL),
        arguments(ARRAY_BOOL, ARRAY_STRING, ARRAY2_STRING),
        arguments(ARRAY_BOOL, ARRAY_BLOB, ARRAY2_BLOB),
        arguments(ARRAY_BOOL, ARRAY_NOTHING, ARRAY2_NOTHING),
        arguments(ARRAY_BOOL, ARRAY_PERSON, ARRAY2_PERSON),
        arguments(ARRAY_BOOL, ARRAY_A, ARRAY2_A),

        arguments(ARRAY_STRING, ARRAY_BOOL, ARRAY2_BOOL),
        arguments(ARRAY_STRING, ARRAY_STRING, ARRAY2_STRING),
        arguments(ARRAY_STRING, ARRAY_BLOB, ARRAY2_BLOB),
        arguments(ARRAY_STRING, ARRAY_NOTHING, ARRAY2_NOTHING),
        arguments(ARRAY_STRING, ARRAY_PERSON, ARRAY2_PERSON),
        arguments(ARRAY_STRING, ARRAY_A, ARRAY2_A),

        arguments(ARRAY_BLOB, ARRAY_BOOL, ARRAY2_BOOL),
        arguments(ARRAY_BLOB, ARRAY_STRING, ARRAY2_STRING),
        arguments(ARRAY_BLOB, ARRAY_BLOB, ARRAY2_BLOB),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, ARRAY2_NOTHING),
        arguments(ARRAY_BLOB, ARRAY_PERSON, ARRAY2_PERSON),
        arguments(ARRAY_BLOB, ARRAY_A, ARRAY2_A),

        arguments(ARRAY_NOTHING, ARRAY_BOOL, ARRAY2_BOOL),
        arguments(ARRAY_NOTHING, ARRAY_STRING, ARRAY2_STRING),
        arguments(ARRAY_NOTHING, ARRAY_BLOB, ARRAY2_BLOB),
        arguments(ARRAY_NOTHING, ARRAY_NOTHING, ARRAY2_NOTHING),
        arguments(ARRAY_NOTHING, ARRAY_PERSON, ARRAY2_PERSON),
        arguments(ARRAY_NOTHING, ARRAY_A, ARRAY2_A),

        arguments(ARRAY_PERSON, ARRAY_BOOL, ARRAY2_BOOL),
        arguments(ARRAY_PERSON, ARRAY_STRING, ARRAY2_STRING),
        arguments(ARRAY_PERSON, ARRAY_BLOB, ARRAY2_BLOB),
        arguments(ARRAY_PERSON, ARRAY_NOTHING, ARRAY2_NOTHING),
        arguments(ARRAY_PERSON, ARRAY_PERSON, ARRAY2_PERSON),
        arguments(ARRAY_PERSON, ARRAY_A, ARRAY2_A),

        arguments(ARRAY_A, ARRAY_BOOL, ARRAY2_BOOL),
        arguments(ARRAY_A, ARRAY_STRING, ARRAY2_STRING),
        arguments(ARRAY_A, ARRAY_BLOB, ARRAY2_BLOB),
        arguments(ARRAY_A, ARRAY_NOTHING, ARRAY2_NOTHING),
        arguments(ARRAY_A, ARRAY_PERSON, ARRAY2_PERSON),
        arguments(ARRAY_A, ARRAY_A, ARRAY2_A)
    );
  }

  @ParameterizedTest
  @MethodSource("coreDepth_test_data")
  public void coreDepth(Type type, int expected) {
    assertThat(type.coreDepth())
        .isEqualTo(expected);
  }

  public static List<Arguments> coreDepth_test_data() {
    return List.of(
        arguments(BOOL, 0),
        arguments(STRING, 0),
        arguments(BLOB, 0),
        arguments(NOTHING, 0),
        arguments(PERSON, 0),
        arguments(A, 0),

        arguments(ARRAY_BOOL, 1),
        arguments(ARRAY_STRING, 1),
        arguments(ARRAY_BLOB, 1),
        arguments(ARRAY_NOTHING, 1),
        arguments(ARRAY_PERSON, 1),
        arguments(ARRAY_A, 1),

        arguments(ARRAY2_BOOL, 2),
        arguments(ARRAY2_STRING, 2),
        arguments(ARRAY2_BLOB, 2),
        arguments(ARRAY2_NOTHING, 2),
        arguments(ARRAY2_PERSON, 2),
        arguments(ARRAY2_A, 2));
  }

  @ParameterizedTest
  @MethodSource("changeCoreDepthBy_test_data_with_illegal_values")
  public void changeCoreDepthBy_fails_for(Type type, int change) {
    assertCall(() -> type.changeCoreDepthBy(change))
        .throwsException(IllegalArgumentException.class);
  }

  public static List<Arguments> changeCoreDepthBy_test_data_with_illegal_values() {
    return List.of(
        arguments(BOOL, -2),
        arguments(STRING, -2),
        arguments(NOTHING, -2),
        arguments(PERSON, -2),
        arguments(A, -2),

        arguments(BOOL, -1),
        arguments(STRING, -1),
        arguments(NOTHING, -1),
        arguments(PERSON, -1),
        arguments(A, -1),

        //
        arguments(ARRAY_BOOL, -2),
        arguments(ARRAY_STRING, -2),
        arguments(ARRAY_NOTHING, -2),
        arguments(ARRAY_PERSON, -2),
        arguments(ARRAY_A, -2),

        //
        arguments(ARRAY2_BOOL, -3),
        arguments(ARRAY2_STRING, -3),
        arguments(ARRAY2_BLOB, -3),
        arguments(ARRAY2_NOTHING, -3),
        arguments(ARRAY2_PERSON, -3),
        arguments(ARRAY2_A, -3)
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
        arguments(BOOL, 0, BOOL),
        arguments(STRING, 0, STRING),
        arguments(NOTHING, 0, NOTHING),
        arguments(PERSON, 0, PERSON),
        arguments(A, 0, A),

        arguments(BOOL, 1, ARRAY_BOOL),
        arguments(STRING, 1, ARRAY_STRING),
        arguments(NOTHING, 1, ARRAY_NOTHING),
        arguments(PERSON, 1, ARRAY_PERSON),
        arguments(A, 1, ARRAY_A),

        arguments(BOOL, 2, ARRAY2_BOOL),
        arguments(STRING, 2, ARRAY2_STRING),
        arguments(NOTHING, 2, ARRAY2_NOTHING),
        arguments(PERSON, 2, ARRAY2_PERSON),
        arguments(A, 2, ARRAY2_A),

        //
        arguments(ARRAY_BOOL, -1, BOOL),
        arguments(ARRAY_STRING, -1, STRING),
        arguments(ARRAY_BLOB, -1, BLOB),
        arguments(ARRAY_NOTHING, -1, NOTHING),
        arguments(ARRAY_PERSON, -1, PERSON),
        arguments(ARRAY_A, -1, A),

        arguments(ARRAY_BOOL, 0, ARRAY_BOOL),
        arguments(ARRAY_STRING, 0, ARRAY_STRING),
        arguments(ARRAY_BLOB, 0, ARRAY_BLOB),
        arguments(ARRAY_NOTHING, 0, ARRAY_NOTHING),
        arguments(ARRAY_PERSON, 0, ARRAY_PERSON),
        arguments(ARRAY_A, 0, ARRAY_A),

        arguments(ARRAY_BOOL, 1, ARRAY2_BOOL),
        arguments(ARRAY_STRING, 1, ARRAY2_STRING),
        arguments(ARRAY_BLOB, 1, ARRAY2_BLOB),
        arguments(ARRAY_NOTHING, 1, ARRAY2_NOTHING),
        arguments(ARRAY_PERSON, 1, ARRAY2_PERSON),
        arguments(ARRAY_A, 1, ARRAY2_A),

        //
        arguments(ARRAY2_BOOL, -2, BOOL),
        arguments(ARRAY2_STRING, -2, STRING),
        arguments(ARRAY2_BLOB, -2, BLOB),
        arguments(ARRAY2_NOTHING, -2, NOTHING),
        arguments(ARRAY2_PERSON, -2, PERSON),
        arguments(ARRAY2_A, -2, A),

        arguments(ARRAY2_BOOL, -1, ARRAY_BOOL),
        arguments(ARRAY2_STRING, -1, ARRAY_STRING),
        arguments(ARRAY2_BLOB, -1, ARRAY_BLOB),
        arguments(ARRAY2_NOTHING, -1, ARRAY_NOTHING),
        arguments(ARRAY2_PERSON, -1, ARRAY_PERSON),
        arguments(ARRAY2_A, -1, ARRAY_A),

        arguments(ARRAY2_BOOL, 0, ARRAY2_BOOL),
        arguments(ARRAY2_STRING, 0, ARRAY2_STRING),
        arguments(ARRAY2_BLOB, 0, ARRAY2_BLOB),
        arguments(ARRAY2_NOTHING, 0, ARRAY2_NOTHING),
        arguments(ARRAY2_PERSON, 0, ARRAY2_PERSON),
        arguments(ARRAY2_A, 0, ARRAY2_A)
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
        arguments(BOOL, false),
        arguments(STRING, false),
        arguments(BLOB, false),
        arguments(NOTHING, false),
        arguments(PERSON, false),
        arguments(ARRAY_BOOL, false),
        arguments(ARRAY_STRING, false),
        arguments(ARRAY_BLOB, false),
        arguments(ARRAY_NOTHING, false),
        arguments(ARRAY_PERSON, false),
        arguments(ARRAY2_BOOL, false),
        arguments(ARRAY2_STRING, false),
        arguments(ARRAY2_BLOB, false),
        arguments(ARRAY2_NOTHING, false),
        arguments(ARRAY2_PERSON, false),

        arguments(A, true),
        arguments(ARRAY_A, true),
        arguments(ARRAY2_A, true),
        arguments(B, true),
        arguments(ARRAY_B, true),
        arguments(ARRAY2_B, true)
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
        arguments(BOOL, false),
        arguments(STRING, false),
        arguments(BLOB, false),
        arguments(NOTHING, false),
        arguments(PERSON, false),
        arguments(A, false),

        arguments(ARRAY_STRING, true),
        arguments(ARRAY_BOOL, true),
        arguments(ARRAY_BLOB, true),
        arguments(ARRAY_NOTHING, true),
        arguments(ARRAY_PERSON, true),
        arguments(ARRAY_A, true),
        arguments(ARRAY2_BOOL, true),
        arguments(ARRAY2_STRING, true),
        arguments(ARRAY2_BLOB, true),
        arguments(ARRAY2_NOTHING, true),
        arguments(ARRAY2_PERSON, true),
        arguments(ARRAY2_A, true)
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
        arguments(BOOL, null),
        arguments(STRING, null),
        arguments(BLOB, null),
        arguments(NOTHING, null),
        arguments(PERSON, STRING),
        arguments(A, null),

        arguments(ARRAY_BOOL, null),
        arguments(ARRAY_STRING, null),
        arguments(ARRAY_BLOB, null),
        arguments(ARRAY_NOTHING, null),
        arguments(ARRAY_PERSON, ARRAY_STRING),
        arguments(ARRAY_A, null),

        arguments(ARRAY2_BOOL, null),
        arguments(ARRAY2_STRING, null),
        arguments(ARRAY2_BLOB, null),
        arguments(ARRAY2_NOTHING, null),
        arguments(ARRAY2_PERSON, ARRAY2_STRING),
        arguments(ARRAY2_A, null)
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
        arguments(list(STRING)),
        arguments(list(BOOL)),
        arguments(list(
            STRING, PERSON)),
        arguments(list(NOTHING)),
        arguments(list(A)),
        arguments(list(ARRAY_BOOL)),
        arguments(list(ARRAY_STRING)),
        arguments(list(
            ARRAY_STRING, ARRAY_PERSON)),
        arguments(list(ARRAY_NOTHING)),
        arguments(list(ARRAY_A)),
        arguments(list(ARRAY2_BOOL)),
        arguments(list(ARRAY2_STRING)),
        arguments(list(
            ARRAY2_STRING, ARRAY2_PERSON)),
        arguments(list(ARRAY2_NOTHING)),
        arguments(list(ARRAY2_A)));
  }

  @ParameterizedTest
  @MethodSource("isAssignableFrom_test_data")
  public void isAssignableFrom(Type destination, Type source, boolean expected) {
    assertThat(destination.isAssignableFrom(source))
        .isEqualTo(expected);
  }

  public static List<Arguments> isAssignableFrom_test_data() {
    return List.of(
        arguments(BOOL, BOOL, true),
        arguments(BOOL, STRING, false),
        arguments(BOOL, BLOB, false),
        arguments(BOOL, PERSON, false),
        arguments(BOOL, NOTHING, true),
        arguments(BOOL, A, false),
        arguments(BOOL, ARRAY_BOOL, false),
        arguments(BOOL, ARRAY_STRING, false),
        arguments(BOOL, ARRAY_BLOB, false),
        arguments(BOOL, ARRAY_PERSON, false),
        arguments(BOOL, ARRAY_NOTHING, false),
        arguments(BOOL, ARRAY_A, false),
        arguments(BOOL, ARRAY2_BOOL, false),
        arguments(BOOL, ARRAY2_STRING, false),
        arguments(BOOL, ARRAY2_BLOB, false),
        arguments(BOOL, ARRAY2_PERSON, false),
        arguments(BOOL, ARRAY2_NOTHING, false),
        arguments(BOOL, ARRAY2_A, false),

        arguments(STRING, BOOL, false),
        arguments(STRING, STRING, true),
        arguments(STRING, BLOB, false),
        arguments(STRING, PERSON, true),
        arguments(STRING, NOTHING, true),
        arguments(STRING, A, false),
        arguments(STRING, ARRAY_BOOL, false),
        arguments(STRING, ARRAY_STRING, false),
        arguments(STRING, ARRAY_BLOB, false),
        arguments(STRING, ARRAY_PERSON, false),
        arguments(STRING, ARRAY_NOTHING, false),
        arguments(STRING, ARRAY_A, false),
        arguments(STRING, ARRAY2_BOOL, false),
        arguments(STRING, ARRAY2_STRING, false),
        arguments(STRING, ARRAY2_BLOB, false),
        arguments(STRING, ARRAY2_PERSON, false),
        arguments(STRING, ARRAY2_NOTHING, false),
        arguments(STRING, ARRAY2_A, false),

        arguments(BLOB, BOOL, false),
        arguments(BLOB, STRING, false),
        arguments(BLOB, BLOB, true),
        arguments(BLOB, PERSON, false),
        arguments(BLOB, NOTHING, true),
        arguments(BLOB, A, false),
        arguments(BLOB, ARRAY_BOOL, false),
        arguments(BLOB, ARRAY_STRING, false),
        arguments(BLOB, ARRAY_BLOB, false),
        arguments(BLOB, ARRAY_PERSON, false),
        arguments(BLOB, ARRAY_NOTHING, false),
        arguments(BLOB, ARRAY_A, false),
        arguments(BLOB, ARRAY2_BOOL, false),
        arguments(BLOB, ARRAY2_STRING, false),
        arguments(BLOB, ARRAY2_BLOB, false),
        arguments(BLOB, ARRAY2_PERSON, false),
        arguments(BLOB, ARRAY2_NOTHING, false),
        arguments(BLOB, ARRAY2_A, false),

        arguments(PERSON, BOOL, false),
        arguments(PERSON, STRING, false),
        arguments(PERSON, BLOB, false),
        arguments(PERSON, PERSON, true),
        arguments(PERSON, NOTHING, true),
        arguments(PERSON, A, false),
        arguments(PERSON, ARRAY_BOOL, false),
        arguments(PERSON, ARRAY_STRING, false),
        arguments(PERSON, ARRAY_BLOB, false),
        arguments(PERSON, ARRAY_PERSON, false),
        arguments(PERSON, ARRAY_NOTHING, false),
        arguments(PERSON, ARRAY_A, false),
        arguments(PERSON, ARRAY2_BOOL, false),
        arguments(PERSON, ARRAY2_STRING, false),
        arguments(PERSON, ARRAY2_BLOB, false),
        arguments(PERSON, ARRAY2_PERSON, false),
        arguments(PERSON, ARRAY2_NOTHING, false),
        arguments(PERSON, ARRAY2_A, false),

        arguments(NOTHING, BOOL, false),
        arguments(NOTHING, STRING, false),
        arguments(NOTHING, BLOB, false),
        arguments(NOTHING, PERSON, false),
        arguments(NOTHING, NOTHING, true),
        arguments(NOTHING, A, false),
        arguments(NOTHING, ARRAY_BOOL, false),
        arguments(NOTHING, ARRAY_STRING, false),
        arguments(NOTHING, ARRAY_BLOB, false),
        arguments(NOTHING, ARRAY_PERSON, false),
        arguments(NOTHING, ARRAY_NOTHING, false),
        arguments(NOTHING, ARRAY_A, false),
        arguments(NOTHING, ARRAY2_BOOL, false),
        arguments(NOTHING, ARRAY2_STRING, false),
        arguments(NOTHING, ARRAY2_BLOB, false),
        arguments(NOTHING, ARRAY2_PERSON, false),
        arguments(NOTHING, ARRAY2_NOTHING, false),
        arguments(NOTHING, ARRAY2_A, false),

        arguments(A, BOOL, false),
        arguments(A, STRING, false),
        arguments(A, BLOB, false),
        arguments(A, PERSON, false),
        arguments(A, NOTHING, true),
        arguments(A, A, true),
        arguments(A, B, false),
        arguments(A, ARRAY_BOOL, false),
        arguments(A, ARRAY_STRING, false),
        arguments(A, ARRAY_BLOB, false),
        arguments(A, ARRAY_PERSON, false),
        arguments(A, ARRAY_NOTHING, false),
        arguments(A, ARRAY_A, false),
        arguments(A, ARRAY_B, false),
        arguments(A, ARRAY2_BOOL, false),
        arguments(A, ARRAY2_STRING, false),
        arguments(A, ARRAY2_BLOB, false),
        arguments(A, ARRAY2_PERSON, false),
        arguments(A, ARRAY2_NOTHING, false),
        arguments(A, ARRAY2_A, false),
        arguments(A, ARRAY2_B, false),

        arguments(ARRAY_STRING, BOOL, false),
        arguments(ARRAY_STRING, STRING, false),
        arguments(ARRAY_STRING, BLOB, false),
        arguments(ARRAY_STRING, PERSON, false),
        arguments(ARRAY_STRING, NOTHING, true),
        arguments(ARRAY_STRING, A, false),
        arguments(ARRAY_STRING, ARRAY_BOOL, false),
        arguments(ARRAY_STRING, ARRAY_STRING, true),
        arguments(ARRAY_STRING, ARRAY_BLOB, false),
        arguments(ARRAY_STRING, ARRAY_PERSON, true),
        arguments(ARRAY_STRING, ARRAY_NOTHING, true),
        arguments(ARRAY_STRING, ARRAY_A, false),
        arguments(ARRAY_STRING, ARRAY2_BOOL, false),
        arguments(ARRAY_STRING, ARRAY2_STRING, false),
        arguments(ARRAY_STRING, ARRAY2_BLOB, false),
        arguments(ARRAY_STRING, ARRAY2_PERSON, false),
        arguments(ARRAY_STRING, ARRAY2_NOTHING, false),
        arguments(ARRAY_STRING, ARRAY2_A, false),

        arguments(ARRAY_BOOL, BOOL, false),
        arguments(ARRAY_BOOL, STRING, false),
        arguments(ARRAY_BOOL, BLOB, false),
        arguments(ARRAY_BOOL, PERSON, false),
        arguments(ARRAY_BOOL, NOTHING, true),
        arguments(ARRAY_BOOL, A, false),
        arguments(ARRAY_BOOL, ARRAY_BOOL, true),
        arguments(ARRAY_BOOL, ARRAY_STRING, false),
        arguments(ARRAY_BOOL, ARRAY_BLOB, false),
        arguments(ARRAY_BOOL, ARRAY_PERSON, false),
        arguments(ARRAY_BOOL, ARRAY_NOTHING, true),
        arguments(ARRAY_BOOL, ARRAY_A, false),
        arguments(ARRAY_BOOL, ARRAY2_BOOL, false),
        arguments(ARRAY_BOOL, ARRAY2_STRING, false),
        arguments(ARRAY_BOOL, ARRAY2_BLOB, false),
        arguments(ARRAY_BOOL, ARRAY2_PERSON, false),
        arguments(ARRAY_BOOL, ARRAY2_NOTHING, false),
        arguments(ARRAY_BOOL, ARRAY2_A, false),

        arguments(ARRAY_BLOB, BOOL, false),
        arguments(ARRAY_BLOB, STRING, false),
        arguments(ARRAY_BLOB, BLOB, false),
        arguments(ARRAY_BLOB, PERSON, false),
        arguments(ARRAY_BLOB, NOTHING, true),
        arguments(ARRAY_BLOB, A, false),
        arguments(ARRAY_BLOB, ARRAY_BOOL, false),
        arguments(ARRAY_BLOB, ARRAY_STRING, false),
        arguments(ARRAY_BLOB, ARRAY_BLOB, true),
        arguments(ARRAY_BLOB, ARRAY_PERSON, false),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, true),
        arguments(ARRAY_BLOB, ARRAY_A, false),
        arguments(ARRAY_BLOB, ARRAY2_BOOL, false),
        arguments(ARRAY_BLOB, ARRAY2_STRING, false),
        arguments(ARRAY_BLOB, ARRAY2_BLOB, false),
        arguments(ARRAY_BLOB, ARRAY2_PERSON, false),
        arguments(ARRAY_BLOB, ARRAY2_NOTHING, false),
        arguments(ARRAY_BLOB, ARRAY2_A, false),

        arguments(ARRAY_PERSON, BOOL, false),
        arguments(ARRAY_PERSON, STRING, false),
        arguments(ARRAY_PERSON, BLOB, false),
        arguments(ARRAY_PERSON, PERSON, false),
        arguments(ARRAY_PERSON, NOTHING, true),
        arguments(ARRAY_PERSON, A, false),
        arguments(ARRAY_PERSON, ARRAY_BOOL, false),
        arguments(ARRAY_PERSON, ARRAY_STRING, false),
        arguments(ARRAY_PERSON, ARRAY_BLOB, false),
        arguments(ARRAY_PERSON, ARRAY_PERSON, true),
        arguments(ARRAY_PERSON, ARRAY_NOTHING, true),
        arguments(ARRAY_PERSON, ARRAY_A, false),
        arguments(ARRAY_PERSON, ARRAY2_BOOL, false),
        arguments(ARRAY_PERSON, ARRAY2_STRING, false),
        arguments(ARRAY_PERSON, ARRAY2_BLOB, false),
        arguments(ARRAY_PERSON, ARRAY2_PERSON, false),
        arguments(ARRAY_PERSON, ARRAY2_NOTHING, false),
        arguments(ARRAY_PERSON, ARRAY2_A, false),

        arguments(ARRAY_NOTHING, BOOL, false),
        arguments(ARRAY_NOTHING, STRING, false),
        arguments(ARRAY_NOTHING, BLOB, false),
        arguments(ARRAY_NOTHING, PERSON, false),
        arguments(ARRAY_NOTHING, NOTHING, true),
        arguments(ARRAY_NOTHING, A, false),
        arguments(ARRAY_NOTHING, ARRAY_BOOL, false),
        arguments(ARRAY_NOTHING, ARRAY_STRING, false),
        arguments(ARRAY_NOTHING, ARRAY_BLOB, false),
        arguments(ARRAY_NOTHING, ARRAY_PERSON, false),
        arguments(ARRAY_NOTHING, ARRAY_NOTHING, true),
        arguments(ARRAY_NOTHING, ARRAY_A, false),
        arguments(ARRAY_NOTHING, ARRAY2_BOOL, false),
        arguments(ARRAY_NOTHING, ARRAY2_STRING, false),
        arguments(ARRAY_NOTHING, ARRAY2_BLOB, false),
        arguments(ARRAY_NOTHING, ARRAY2_PERSON, false),
        arguments(ARRAY_NOTHING, ARRAY2_NOTHING, false),
        arguments(ARRAY_NOTHING, ARRAY2_A, false),

        arguments(ARRAY_A, BOOL, false),
        arguments(ARRAY_A, STRING, false),
        arguments(ARRAY_A, BLOB, false),
        arguments(ARRAY_A, PERSON, false),
        arguments(ARRAY_A, NOTHING, true),
        arguments(ARRAY_A, A, false),
        arguments(ARRAY_A, B, false),
        arguments(ARRAY_A, ARRAY_BOOL, false),
        arguments(ARRAY_A, ARRAY_STRING, false),
        arguments(ARRAY_A, ARRAY_BLOB, false),
        arguments(ARRAY_A, ARRAY_PERSON, false),
        arguments(ARRAY_A, ARRAY_NOTHING, true),
        arguments(ARRAY_A, ARRAY_A, true),
        arguments(ARRAY_A, ARRAY_B, false),
        arguments(ARRAY_A, ARRAY2_BOOL, false),
        arguments(ARRAY_A, ARRAY2_STRING, false),
        arguments(ARRAY_A, ARRAY2_BLOB, false),
        arguments(ARRAY_A, ARRAY2_PERSON, false),
        arguments(ARRAY_A, ARRAY2_NOTHING, false),
        arguments(ARRAY_A, ARRAY2_A, false),
        arguments(ARRAY_A, ARRAY2_B, false),

        arguments(ARRAY2_BOOL, BOOL, false),
        arguments(ARRAY2_BOOL, STRING, false),
        arguments(ARRAY2_BOOL, BLOB, false),
        arguments(ARRAY2_BOOL, PERSON, false),
        arguments(ARRAY2_BOOL, NOTHING, true),
        arguments(ARRAY2_BOOL, A, false),
        arguments(ARRAY2_BOOL, ARRAY_BOOL, false),
        arguments(ARRAY2_BOOL, ARRAY_STRING, false),
        arguments(ARRAY2_BOOL, ARRAY_BLOB, false),
        arguments(ARRAY2_BOOL, ARRAY_PERSON, false),
        arguments(ARRAY2_BOOL, ARRAY_NOTHING, true),
        arguments(ARRAY2_BOOL, ARRAY_A, false),
        arguments(ARRAY2_BOOL, ARRAY2_BOOL, true),
        arguments(ARRAY2_BOOL, ARRAY2_STRING, false),
        arguments(ARRAY2_BOOL, ARRAY2_BLOB, false),
        arguments(ARRAY2_BOOL, ARRAY2_PERSON, false),
        arguments(ARRAY2_BOOL, ARRAY2_NOTHING, true),
        arguments(ARRAY2_BOOL, ARRAY2_A, false),

        arguments(ARRAY2_STRING, BOOL, false),
        arguments(ARRAY2_STRING, STRING, false),
        arguments(ARRAY2_STRING, BLOB, false),
        arguments(ARRAY2_STRING, PERSON, false),
        arguments(ARRAY2_STRING, NOTHING, true),
        arguments(ARRAY2_STRING, A, false),
        arguments(ARRAY2_STRING, ARRAY_BOOL, false),
        arguments(ARRAY2_STRING, ARRAY_STRING, false),
        arguments(ARRAY2_STRING, ARRAY_BLOB, false),
        arguments(ARRAY2_STRING, ARRAY_PERSON, false),
        arguments(ARRAY2_STRING, ARRAY_NOTHING, true),
        arguments(ARRAY2_STRING, ARRAY_A, false),
        arguments(ARRAY2_STRING, ARRAY2_BOOL, false),
        arguments(ARRAY2_STRING, ARRAY2_STRING, true),
        arguments(ARRAY2_STRING, ARRAY2_BLOB, false),
        arguments(ARRAY2_STRING, ARRAY2_PERSON, true),
        arguments(ARRAY2_STRING, ARRAY2_NOTHING, true),
        arguments(ARRAY2_STRING, ARRAY2_A, false),

        arguments(ARRAY2_BLOB, BOOL, false),
        arguments(ARRAY2_BLOB, STRING, false),
        arguments(ARRAY2_BLOB, BLOB, false),
        arguments(ARRAY2_BLOB, PERSON, false),
        arguments(ARRAY2_BLOB, NOTHING, true),
        arguments(ARRAY2_BLOB, A, false),
        arguments(ARRAY2_BLOB, ARRAY_BOOL, false),
        arguments(ARRAY2_BLOB, ARRAY_STRING, false),
        arguments(ARRAY2_BLOB, ARRAY_BLOB, false),
        arguments(ARRAY2_BLOB, ARRAY_PERSON, false),
        arguments(ARRAY2_BLOB, ARRAY_NOTHING, true),
        arguments(ARRAY2_BLOB, ARRAY_A, false),
        arguments(ARRAY2_BLOB, ARRAY2_BOOL, false),
        arguments(ARRAY2_BLOB, ARRAY2_STRING, false),
        arguments(ARRAY2_BLOB, ARRAY2_BLOB, true),
        arguments(ARRAY2_BLOB, ARRAY2_PERSON, false),
        arguments(ARRAY2_BLOB, ARRAY2_NOTHING, true),
        arguments(ARRAY2_BLOB, ARRAY2_A, false),

        arguments(ARRAY2_PERSON, BOOL, false),
        arguments(ARRAY2_PERSON, STRING, false),
        arguments(ARRAY2_PERSON, BLOB, false),
        arguments(ARRAY2_PERSON, PERSON, false),
        arguments(ARRAY2_PERSON, NOTHING, true),
        arguments(ARRAY2_PERSON, A, false),
        arguments(ARRAY2_PERSON, ARRAY_BOOL, false),
        arguments(ARRAY2_PERSON, ARRAY_STRING, false),
        arguments(ARRAY2_PERSON, ARRAY_BLOB, false),
        arguments(ARRAY2_PERSON, ARRAY_PERSON, false),
        arguments(ARRAY2_PERSON, ARRAY_NOTHING, true),
        arguments(ARRAY2_PERSON, ARRAY_A, false),
        arguments(ARRAY2_PERSON, ARRAY2_BOOL, false),
        arguments(ARRAY2_PERSON, ARRAY2_STRING, false),
        arguments(ARRAY2_PERSON, ARRAY2_BLOB, false),
        arguments(ARRAY2_PERSON, ARRAY2_PERSON, true),
        arguments(ARRAY2_PERSON, ARRAY2_NOTHING, true),
        arguments(ARRAY2_PERSON, ARRAY2_A, false),

        arguments(ARRAY2_NOTHING, BOOL, false),
        arguments(ARRAY2_NOTHING, STRING, false),
        arguments(ARRAY2_NOTHING, BLOB, false),
        arguments(ARRAY2_NOTHING, PERSON, false),
        arguments(ARRAY2_NOTHING, NOTHING, true),
        arguments(ARRAY2_NOTHING, A, false),
        arguments(ARRAY2_NOTHING, ARRAY_BOOL, false),
        arguments(ARRAY2_NOTHING, ARRAY_STRING, false),
        arguments(ARRAY2_NOTHING, ARRAY_BLOB, false),
        arguments(ARRAY2_NOTHING, ARRAY_PERSON, false),
        arguments(ARRAY2_NOTHING, ARRAY_NOTHING, true),
        arguments(ARRAY2_NOTHING, ARRAY_A, false),
        arguments(ARRAY2_NOTHING, ARRAY2_BOOL, false),
        arguments(ARRAY2_NOTHING, ARRAY2_STRING, false),
        arguments(ARRAY2_NOTHING, ARRAY2_BLOB, false),
        arguments(ARRAY2_NOTHING, ARRAY2_PERSON, false),
        arguments(ARRAY2_NOTHING, ARRAY2_NOTHING, true),
        arguments(ARRAY2_NOTHING, ARRAY2_A, false),

        arguments(ARRAY2_A, BOOL, false),
        arguments(ARRAY2_A, STRING, false),
        arguments(ARRAY2_A, BLOB, false),
        arguments(ARRAY2_A, PERSON, false),
        arguments(ARRAY2_A, NOTHING, true),
        arguments(ARRAY2_A, A, false),
        arguments(ARRAY2_A, B, false),
        arguments(ARRAY2_A, ARRAY_BOOL, false),
        arguments(ARRAY2_A, ARRAY_STRING, false),
        arguments(ARRAY2_A, ARRAY_BLOB, false),
        arguments(ARRAY2_A, ARRAY_PERSON, false),
        arguments(ARRAY2_A, ARRAY_NOTHING, true),
        arguments(ARRAY2_A, ARRAY_A, false),
        arguments(ARRAY2_A, ARRAY_B, false),
        arguments(ARRAY2_A, ARRAY2_BOOL, false),
        arguments(ARRAY2_A, ARRAY2_STRING, false),
        arguments(ARRAY2_A, ARRAY2_BLOB, false),
        arguments(ARRAY2_A, ARRAY2_PERSON, false),
        arguments(ARRAY2_A, ARRAY2_NOTHING, true),
        arguments(ARRAY2_A, ARRAY2_A, true),
        arguments(ARRAY2_A, ARRAY2_B, false)
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
        arguments(BOOL, BOOL, true),
        arguments(BOOL, STRING, false),
        arguments(BOOL, BLOB, false),
        arguments(BOOL, PERSON, false),
        arguments(BOOL, NOTHING, true),
        arguments(BOOL, A, false),
        arguments(BOOL, ARRAY_BOOL, false),
        arguments(BOOL, ARRAY_STRING, false),
        arguments(BOOL, ARRAY_BLOB, false),
        arguments(BOOL, ARRAY_PERSON, false),
        arguments(BOOL, ARRAY_NOTHING, false),
        arguments(BOOL, ARRAY_A, false),
        arguments(BOOL, ARRAY2_BOOL, false),
        arguments(BOOL, ARRAY2_STRING, false),
        arguments(BOOL, ARRAY2_BLOB, false),
        arguments(BOOL, ARRAY2_PERSON, false),
        arguments(BOOL, ARRAY2_NOTHING, false),
        arguments(BOOL, ARRAY2_A, false),

        arguments(STRING, BOOL, false),
        arguments(STRING, STRING, true),
        arguments(STRING, BLOB, false),
        arguments(STRING, PERSON, true),
        arguments(STRING, NOTHING, true),
        arguments(STRING, A, false),
        arguments(STRING, ARRAY_BOOL, false),
        arguments(STRING, ARRAY_STRING, false),
        arguments(STRING, ARRAY_BLOB, false),
        arguments(STRING, ARRAY_PERSON, false),
        arguments(STRING, ARRAY_NOTHING, false),
        arguments(STRING, ARRAY_A, false),
        arguments(STRING, ARRAY2_BOOL, false),
        arguments(STRING, ARRAY2_STRING, false),
        arguments(STRING, ARRAY2_BLOB, false),
        arguments(STRING, ARRAY2_PERSON, false),
        arguments(STRING, ARRAY2_NOTHING, false),
        arguments(STRING, ARRAY2_A, false),

        arguments(BLOB, BOOL, false),
        arguments(BLOB, STRING, false),
        arguments(BLOB, BLOB, true),
        arguments(BLOB, PERSON, false),
        arguments(BLOB, NOTHING, true),
        arguments(BLOB, A, false),
        arguments(BLOB, ARRAY_BOOL, false),
        arguments(BLOB, ARRAY_STRING, false),
        arguments(BLOB, ARRAY_BLOB, false),
        arguments(BLOB, ARRAY_PERSON, false),
        arguments(BLOB, ARRAY_NOTHING, false),
        arguments(BLOB, ARRAY_A, false),
        arguments(BLOB, ARRAY2_BOOL, false),
        arguments(BLOB, ARRAY2_STRING, false),
        arguments(BLOB, ARRAY2_BLOB, false),
        arguments(BLOB, ARRAY2_PERSON, false),
        arguments(BLOB, ARRAY2_NOTHING, false),
        arguments(BLOB, ARRAY2_A, false),

        arguments(PERSON, BOOL, false),
        arguments(PERSON, STRING, false),
        arguments(PERSON, BLOB, false),
        arguments(PERSON, PERSON, true),
        arguments(PERSON, NOTHING, true),
        arguments(PERSON, A, false),
        arguments(PERSON, ARRAY_BOOL, false),
        arguments(PERSON, ARRAY_STRING, false),
        arguments(PERSON, ARRAY_BLOB, false),
        arguments(PERSON, ARRAY_PERSON, false),
        arguments(PERSON, ARRAY_NOTHING, false),
        arguments(PERSON, ARRAY_A, false),
        arguments(PERSON, ARRAY2_BOOL, false),
        arguments(PERSON, ARRAY2_STRING, false),
        arguments(PERSON, ARRAY2_BLOB, false),
        arguments(PERSON, ARRAY2_PERSON, false),
        arguments(PERSON, ARRAY2_NOTHING, false),
        arguments(PERSON, ARRAY2_A, false),

        arguments(NOTHING, BOOL, false),
        arguments(NOTHING, STRING, false),
        arguments(NOTHING, BLOB, false),
        arguments(NOTHING, PERSON, false),
        arguments(NOTHING, NOTHING, true),
        arguments(NOTHING, A, false),
        arguments(NOTHING, ARRAY_BOOL, false),
        arguments(NOTHING, ARRAY_STRING, false),
        arguments(NOTHING, ARRAY_BLOB, false),
        arguments(NOTHING, ARRAY_PERSON, false),
        arguments(NOTHING, ARRAY_NOTHING, false),
        arguments(NOTHING, ARRAY_A, false),
        arguments(NOTHING, ARRAY2_BOOL, false),
        arguments(NOTHING, ARRAY2_STRING, false),
        arguments(NOTHING, ARRAY2_BLOB, false),
        arguments(NOTHING, ARRAY2_PERSON, false),
        arguments(NOTHING, ARRAY2_NOTHING, false),
        arguments(NOTHING, ARRAY2_A, false),

        arguments(A, BOOL, true),
        arguments(A, STRING, true),
        arguments(A, BLOB, true),
        arguments(A, PERSON, true),
        arguments(A, NOTHING, true),
        arguments(A, A, true),
        arguments(A, B, true),
        arguments(A, ARRAY_BOOL, true),
        arguments(A, ARRAY_STRING, true),
        arguments(A, ARRAY_BLOB, true),
        arguments(A, ARRAY_PERSON, true),
        arguments(A, ARRAY_NOTHING, true),
        arguments(A, ARRAY_A, true),
        arguments(A, ARRAY_B, true),
        arguments(A, ARRAY2_BOOL, true),
        arguments(A, ARRAY2_STRING, true),
        arguments(A, ARRAY2_BLOB, true),
        arguments(A, ARRAY2_PERSON, true),
        arguments(A, ARRAY2_NOTHING, true),
        arguments(A, ARRAY2_A, true),
        arguments(A, ARRAY2_B, true),

        arguments(ARRAY_BOOL, BOOL, false),
        arguments(ARRAY_BOOL, STRING, false),
        arguments(ARRAY_BOOL, BLOB, false),
        arguments(ARRAY_BOOL, PERSON, false),
        arguments(ARRAY_BOOL, NOTHING, true),
        arguments(ARRAY_BOOL, A, false),
        arguments(ARRAY_BOOL, ARRAY_BOOL, true),
        arguments(ARRAY_BOOL, ARRAY_STRING, false),
        arguments(ARRAY_BOOL, ARRAY_BLOB, false),
        arguments(ARRAY_BOOL, ARRAY_PERSON, false),
        arguments(ARRAY_BOOL, ARRAY_NOTHING, true),
        arguments(ARRAY_BOOL, ARRAY_A, false),
        arguments(ARRAY_BOOL, ARRAY2_BOOL, false),
        arguments(ARRAY_BOOL, ARRAY2_STRING, false),
        arguments(ARRAY_BOOL, ARRAY2_BLOB, false),
        arguments(ARRAY_BOOL, ARRAY2_PERSON, false),
        arguments(ARRAY_BOOL, ARRAY2_NOTHING, false),
        arguments(ARRAY_BOOL, ARRAY2_A, false),

        arguments(ARRAY_STRING, BOOL, false),
        arguments(ARRAY_STRING, STRING, false),
        arguments(ARRAY_STRING, BLOB, false),
        arguments(ARRAY_STRING, PERSON, false),
        arguments(ARRAY_STRING, NOTHING, true),
        arguments(ARRAY_STRING, A, false),
        arguments(ARRAY_STRING, ARRAY_BOOL, false),
        arguments(ARRAY_STRING, ARRAY_STRING, true),
        arguments(ARRAY_STRING, ARRAY_BLOB, false),
        arguments(ARRAY_STRING, ARRAY_PERSON, true),
        arguments(ARRAY_STRING, ARRAY_NOTHING, true),
        arguments(ARRAY_STRING, ARRAY_A, false),
        arguments(ARRAY_STRING, ARRAY2_BOOL, false),
        arguments(ARRAY_STRING, ARRAY2_STRING, false),
        arguments(ARRAY_STRING, ARRAY2_BLOB, false),
        arguments(ARRAY_STRING, ARRAY2_PERSON, false),
        arguments(ARRAY_STRING, ARRAY2_NOTHING, false),
        arguments(ARRAY_STRING, ARRAY2_A, false),

        arguments(ARRAY_BLOB, BOOL, false),
        arguments(ARRAY_BLOB, STRING, false),
        arguments(ARRAY_BLOB, BLOB, false),
        arguments(ARRAY_BLOB, PERSON, false),
        arguments(ARRAY_BLOB, NOTHING, true),
        arguments(ARRAY_BLOB, A, false),
        arguments(ARRAY_BLOB, ARRAY_BOOL, false),
        arguments(ARRAY_BLOB, ARRAY_STRING, false),
        arguments(ARRAY_BLOB, ARRAY_BLOB, true),
        arguments(ARRAY_BLOB, ARRAY_PERSON, false),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, true),
        arguments(ARRAY_BLOB, ARRAY_A, false),
        arguments(ARRAY_BLOB, ARRAY2_BOOL, false),
        arguments(ARRAY_BLOB, ARRAY2_STRING, false),
        arguments(ARRAY_BLOB, ARRAY2_BLOB, false),
        arguments(ARRAY_BLOB, ARRAY2_PERSON, false),
        arguments(ARRAY_BLOB, ARRAY2_NOTHING, false),
        arguments(ARRAY_BLOB, ARRAY2_A, false),

        arguments(ARRAY_PERSON, BOOL, false),
        arguments(ARRAY_PERSON, STRING, false),
        arguments(ARRAY_PERSON, BLOB, false),
        arguments(ARRAY_PERSON, PERSON, false),
        arguments(ARRAY_PERSON, NOTHING, true),
        arguments(ARRAY_PERSON, A, false),
        arguments(ARRAY_PERSON, ARRAY_BOOL, false),
        arguments(ARRAY_PERSON, ARRAY_STRING, false),
        arguments(ARRAY_PERSON, ARRAY_BLOB, false),
        arguments(ARRAY_PERSON, ARRAY_PERSON, true),
        arguments(ARRAY_PERSON, ARRAY_NOTHING, true),
        arguments(ARRAY_PERSON, ARRAY_A, false),
        arguments(ARRAY_PERSON, ARRAY2_BOOL, false),
        arguments(ARRAY_PERSON, ARRAY2_STRING, false),
        arguments(ARRAY_PERSON, ARRAY2_BLOB, false),
        arguments(ARRAY_PERSON, ARRAY2_PERSON, false),
        arguments(ARRAY_PERSON, ARRAY2_NOTHING, false),
        arguments(ARRAY_PERSON, ARRAY2_A, false),

        arguments(ARRAY_NOTHING, BOOL, false),
        arguments(ARRAY_NOTHING, STRING, false),
        arguments(ARRAY_NOTHING, BLOB, false),
        arguments(ARRAY_NOTHING, PERSON, false),
        arguments(ARRAY_NOTHING, NOTHING, true),
        arguments(ARRAY_NOTHING, A, false),
        arguments(ARRAY_NOTHING, ARRAY_BOOL, false),
        arguments(ARRAY_NOTHING, ARRAY_STRING, false),
        arguments(ARRAY_NOTHING, ARRAY_BLOB, false),
        arguments(ARRAY_NOTHING, ARRAY_PERSON, false),
        arguments(ARRAY_NOTHING, ARRAY_NOTHING, true),
        arguments(ARRAY_NOTHING, ARRAY_A, false),
        arguments(ARRAY_NOTHING, ARRAY2_BOOL, false),
        arguments(ARRAY_NOTHING, ARRAY2_STRING, false),
        arguments(ARRAY_NOTHING, ARRAY2_BLOB, false),
        arguments(ARRAY_NOTHING, ARRAY2_PERSON, false),
        arguments(ARRAY_NOTHING, ARRAY2_NOTHING, false),
        arguments(ARRAY_NOTHING, ARRAY2_A, false),

        arguments(ARRAY_A, BOOL, false),
        arguments(ARRAY_A, STRING, false),
        arguments(ARRAY_A, BLOB, false),
        arguments(ARRAY_A, PERSON, false),
        arguments(ARRAY_A, NOTHING, true),
        arguments(ARRAY_A, A, false),
        arguments(ARRAY_A, B, false),
        arguments(ARRAY_A, ARRAY_BOOL, true),
        arguments(ARRAY_A, ARRAY_STRING, true),
        arguments(ARRAY_A, ARRAY_BLOB, true),
        arguments(ARRAY_A, ARRAY_PERSON, true),
        arguments(ARRAY_A, ARRAY_NOTHING, true),
        arguments(ARRAY_A, ARRAY_A, true),
        arguments(ARRAY_A, ARRAY_B, true),
        arguments(ARRAY_A, ARRAY2_BOOL, true),
        arguments(ARRAY_A, ARRAY2_STRING, true),
        arguments(ARRAY_A, ARRAY2_BLOB, true),
        arguments(ARRAY_A, ARRAY2_PERSON, true),
        arguments(ARRAY_A, ARRAY2_NOTHING, true),
        arguments(ARRAY_A, ARRAY2_A, true),
        arguments(ARRAY_A, ARRAY2_B, true),

        arguments(ARRAY2_BOOL, BOOL, false),
        arguments(ARRAY2_BOOL, STRING, false),
        arguments(ARRAY2_BOOL, BLOB, false),
        arguments(ARRAY2_BOOL, PERSON, false),
        arguments(ARRAY2_BOOL, NOTHING, true),
        arguments(ARRAY2_BOOL, A, false),
        arguments(ARRAY2_BOOL, ARRAY_BOOL, false),
        arguments(ARRAY2_BOOL, ARRAY_STRING, false),
        arguments(ARRAY2_BOOL, ARRAY_BLOB, false),
        arguments(ARRAY2_BOOL, ARRAY_PERSON, false),
        arguments(ARRAY2_BOOL, ARRAY_NOTHING, true),
        arguments(ARRAY2_BOOL, ARRAY_A, false),
        arguments(ARRAY2_BOOL, ARRAY2_BOOL, true),
        arguments(ARRAY2_BOOL, ARRAY2_STRING, false),
        arguments(ARRAY2_BOOL, ARRAY2_BLOB, false),
        arguments(ARRAY2_BOOL, ARRAY2_PERSON, false),
        arguments(ARRAY2_BOOL, ARRAY2_NOTHING, true),
        arguments(ARRAY2_BOOL, ARRAY2_A, false),

        arguments(ARRAY2_STRING, BOOL, false),
        arguments(ARRAY2_STRING, STRING, false),
        arguments(ARRAY2_STRING, BLOB, false),
        arguments(ARRAY2_STRING, PERSON, false),
        arguments(ARRAY2_STRING, NOTHING, true),
        arguments(ARRAY2_STRING, A, false),
        arguments(ARRAY2_STRING, ARRAY_BOOL, false),
        arguments(ARRAY2_STRING, ARRAY_STRING, false),
        arguments(ARRAY2_STRING, ARRAY_BLOB, false),
        arguments(ARRAY2_STRING, ARRAY_PERSON, false),
        arguments(ARRAY2_STRING, ARRAY_NOTHING, true),
        arguments(ARRAY2_STRING, ARRAY_A, false),
        arguments(ARRAY2_STRING, ARRAY2_BOOL, false),
        arguments(ARRAY2_STRING, ARRAY2_STRING, true),
        arguments(ARRAY2_STRING, ARRAY2_BLOB, false),
        arguments(ARRAY2_STRING, ARRAY2_PERSON, true),
        arguments(ARRAY2_STRING, ARRAY2_NOTHING, true),
        arguments(ARRAY2_STRING, ARRAY2_A, false),

        arguments(ARRAY2_BLOB, BOOL, false),
        arguments(ARRAY2_BLOB, STRING, false),
        arguments(ARRAY2_BLOB, BLOB, false),
        arguments(ARRAY2_BLOB, PERSON, false),
        arguments(ARRAY2_BLOB, NOTHING, true),
        arguments(ARRAY2_BLOB, A, false),
        arguments(ARRAY2_BLOB, ARRAY_BOOL, false),
        arguments(ARRAY2_BLOB, ARRAY_STRING, false),
        arguments(ARRAY2_BLOB, ARRAY_BLOB, false),
        arguments(ARRAY2_BLOB, ARRAY_PERSON, false),
        arguments(ARRAY2_BLOB, ARRAY_NOTHING, true),
        arguments(ARRAY2_BLOB, ARRAY_A, false),
        arguments(ARRAY2_BLOB, ARRAY2_BOOL, false),
        arguments(ARRAY2_BLOB, ARRAY2_STRING, false),
        arguments(ARRAY2_BLOB, ARRAY2_BLOB, true),
        arguments(ARRAY2_BLOB, ARRAY2_PERSON, false),
        arguments(ARRAY2_BLOB, ARRAY2_NOTHING, true),
        arguments(ARRAY2_BLOB, ARRAY2_A, false),

        arguments(ARRAY2_PERSON, BOOL, false),
        arguments(ARRAY2_PERSON, STRING, false),
        arguments(ARRAY2_PERSON, BLOB, false),
        arguments(ARRAY2_PERSON, PERSON, false),
        arguments(ARRAY2_PERSON, NOTHING, true),
        arguments(ARRAY2_PERSON, A, false),
        arguments(ARRAY2_PERSON, ARRAY_BOOL, false),
        arguments(ARRAY2_PERSON, ARRAY_STRING, false),
        arguments(ARRAY2_PERSON, ARRAY_BLOB, false),
        arguments(ARRAY2_PERSON, ARRAY_PERSON, false),
        arguments(ARRAY2_PERSON, ARRAY_NOTHING, true),
        arguments(ARRAY2_PERSON, ARRAY_A, false),
        arguments(ARRAY2_PERSON, ARRAY2_BOOL, false),
        arguments(ARRAY2_PERSON, ARRAY2_STRING, false),
        arguments(ARRAY2_PERSON, ARRAY2_BLOB, false),
        arguments(ARRAY2_PERSON, ARRAY2_PERSON, true),
        arguments(ARRAY2_PERSON, ARRAY2_NOTHING, true),
        arguments(ARRAY2_PERSON, ARRAY2_A, false),

        arguments(ARRAY2_NOTHING, BOOL, false),
        arguments(ARRAY2_NOTHING, STRING, false),
        arguments(ARRAY2_NOTHING, BLOB, false),
        arguments(ARRAY2_NOTHING, PERSON, false),
        arguments(ARRAY2_NOTHING, NOTHING, true),
        arguments(ARRAY2_NOTHING, A, false),
        arguments(ARRAY2_NOTHING, ARRAY_BOOL, false),
        arguments(ARRAY2_NOTHING, ARRAY_STRING, false),
        arguments(ARRAY2_NOTHING, ARRAY_BLOB, false),
        arguments(ARRAY2_NOTHING, ARRAY_PERSON, false),
        arguments(ARRAY2_NOTHING, ARRAY_NOTHING, true),
        arguments(ARRAY2_NOTHING, ARRAY_A, false),
        arguments(ARRAY2_NOTHING, ARRAY2_BOOL, false),
        arguments(ARRAY2_NOTHING, ARRAY2_STRING, false),
        arguments(ARRAY2_NOTHING, ARRAY2_BLOB, false),
        arguments(ARRAY2_NOTHING, ARRAY2_PERSON, false),
        arguments(ARRAY2_NOTHING, ARRAY2_NOTHING, true),
        arguments(ARRAY2_NOTHING, ARRAY2_A, false),

        arguments(ARRAY2_A, BOOL, false),
        arguments(ARRAY2_A, STRING, false),
        arguments(ARRAY2_A, BLOB, false),
        arguments(ARRAY2_A, PERSON, false),
        arguments(ARRAY2_A, NOTHING, true),
        arguments(ARRAY2_A, A, false),
        arguments(ARRAY2_A, B, false),
        arguments(ARRAY2_A, ARRAY_BOOL, false),
        arguments(ARRAY2_A, ARRAY_STRING, false),
        arguments(ARRAY2_A, ARRAY_BLOB, false),
        arguments(ARRAY2_A, ARRAY_PERSON, false),
        arguments(ARRAY2_A, ARRAY_NOTHING, true),
        arguments(ARRAY2_A, ARRAY_A, false),
        arguments(ARRAY2_A, ARRAY_B, false),
        arguments(ARRAY2_A, ARRAY2_BOOL, true),
        arguments(ARRAY2_A, ARRAY2_STRING, true),
        arguments(ARRAY2_A, ARRAY2_BLOB, true),
        arguments(ARRAY2_A, ARRAY2_PERSON, true),
        arguments(ARRAY2_A, ARRAY2_NOTHING, true),
        arguments(ARRAY2_A, ARRAY2_A, true),
        arguments(ARRAY2_A, ARRAY2_B, true));
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
        arguments(BOOL, STRING, Optional.empty()),
        arguments(BOOL, BOOL, Optional.of(BOOL)),
        arguments(BOOL, BLOB, Optional.empty()),
        arguments(BOOL, NOTHING, Optional.of(BOOL)),
        arguments(BOOL, A, Optional.empty()),
        arguments(BOOL, ARRAY_STRING, Optional.empty()),
        arguments(BOOL, ARRAY_BOOL, Optional.empty()),
        arguments(BOOL, ARRAY_BLOB, Optional.empty()),
        arguments(BOOL, ARRAY_NOTHING, Optional.empty()),
        arguments(BOOL, ARRAY_A, Optional.empty()),

        arguments(STRING, STRING, Optional.of(STRING)),
        arguments(STRING, BLOB, Optional.empty()),
        arguments(STRING, NOTHING, Optional.of(STRING)),
        arguments(STRING, A, Optional.empty()),
        arguments(STRING, ARRAY_STRING, Optional.empty()),
        arguments(STRING, ARRAY_BOOL, Optional.empty()),
        arguments(STRING, ARRAY_BLOB, Optional.empty()),
        arguments(STRING, ARRAY_NOTHING, Optional.empty()),
        arguments(STRING, ARRAY_A, Optional.empty()),

        arguments(BLOB, BLOB, Optional.of(BLOB)),
        arguments(BLOB, NOTHING, Optional.of(BLOB)),
        arguments(BLOB, A, Optional.empty()),
        arguments(BLOB, ARRAY_STRING, Optional.empty()),
        arguments(BLOB, ARRAY_BLOB, Optional.empty()),
        arguments(BLOB, ARRAY_NOTHING, Optional.empty()),
        arguments(BLOB, ARRAY_A, Optional.empty()),

        arguments(NOTHING, NOTHING, Optional.of(NOTHING)),
        arguments(NOTHING, A, Optional.of(A)),
        arguments(NOTHING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(NOTHING, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(NOTHING, ARRAY_NOTHING, Optional.of(ARRAY_NOTHING)),
        arguments(NOTHING, ARRAY_A, Optional.of(ARRAY_A)),

        arguments(A, A, Optional.of(A)),
        arguments(A, B, Optional.empty()),
        arguments(A, ARRAY_STRING, Optional.empty()),
        arguments(A, ARRAY_BLOB, Optional.empty()),
        arguments(A, ARRAY_NOTHING, Optional.empty()),
        arguments(A, ARRAY_A, Optional.empty()),
        arguments(A, ARRAY_B, Optional.empty()),

        arguments(ARRAY_STRING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_STRING, ARRAY_BLOB, Optional.empty()),
        arguments(ARRAY_STRING, ARRAY_NOTHING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_STRING, NOTHING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_STRING, ARRAY_A, Optional.empty()),

        arguments(ARRAY_BLOB, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, ARRAY_NOTHING, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, NOTHING, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_BLOB, ARRAY_A, Optional.empty()),

        arguments(ARRAY_NOTHING, ARRAY_NOTHING, Optional.of(ARRAY_NOTHING)),
        arguments(ARRAY_NOTHING, ARRAY2_NOTHING, Optional.of(ARRAY2_NOTHING)),
        arguments(ARRAY_NOTHING, ARRAY_STRING, Optional.of(ARRAY_STRING)),
        arguments(ARRAY_NOTHING, ARRAY_BLOB, Optional.of(ARRAY_BLOB)),
        arguments(ARRAY_NOTHING, ARRAY_A, Optional.of(ARRAY_A)),

        arguments(ARRAY_A, ARRAY_A, Optional.of(ARRAY_A)),
        arguments(ARRAY_A, ARRAY_B, Optional.empty()));
  }

  @ParameterizedTest
  @MethodSource("actualCoreTypeWhenAssignedFrom_test_data")
  public void actualCoreTypeWhenAssignedFrom(Type type, Type assigned, Type expected) {
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
        arguments(A, BOOL, BOOL),
        arguments(A, STRING, STRING),
        arguments(A, BLOB, BLOB),
        arguments(A, PERSON, PERSON),
        arguments(A, NOTHING, NOTHING),
        arguments(A, A, A),
        arguments(A, B, B),

        arguments(A, ARRAY_BOOL, ARRAY_BOOL),
        arguments(A, ARRAY_STRING, ARRAY_STRING),
        arguments(A, ARRAY_BLOB, ARRAY_BLOB),
        arguments(A, ARRAY_PERSON, ARRAY_PERSON),
        arguments(A, ARRAY_NOTHING, ARRAY_NOTHING),
        arguments(A, ARRAY_A, ARRAY_A),
        arguments(A, ARRAY_B, ARRAY_B),

        arguments(A, ARRAY2_BOOL, ARRAY2_BOOL),
        arguments(A, ARRAY2_STRING, ARRAY2_STRING),
        arguments(A, ARRAY2_BLOB, ARRAY2_BLOB),
        arguments(A, ARRAY2_PERSON, ARRAY2_PERSON),
        arguments(A, ARRAY2_NOTHING, ARRAY2_NOTHING),
        arguments(A, ARRAY2_A, ARRAY2_A),
        arguments(A, ARRAY2_B, ARRAY2_B),

        arguments(ARRAY_A, BOOL, null),
        arguments(ARRAY_A, STRING, null),
        arguments(ARRAY_A, BLOB, null),
        arguments(ARRAY_A, PERSON, null),
        arguments(ARRAY_A, NOTHING, NOTHING),
        arguments(ARRAY_A, A, null),
        arguments(ARRAY_A, B, null),

        arguments(ARRAY_A, ARRAY_BOOL, BOOL),
        arguments(ARRAY_A, ARRAY_STRING, STRING),
        arguments(ARRAY_A, ARRAY_BLOB, BLOB),
        arguments(ARRAY_A, ARRAY_PERSON, PERSON),
        arguments(ARRAY_A, ARRAY_NOTHING, NOTHING),
        arguments(ARRAY_A, ARRAY_A, A),
        arguments(ARRAY_A, ARRAY_B, B),

        arguments(ARRAY_A, ARRAY2_BOOL, ARRAY_BOOL),
        arguments(ARRAY_A, ARRAY2_STRING, ARRAY_STRING),
        arguments(ARRAY_A, ARRAY2_BLOB, ARRAY_BLOB),
        arguments(ARRAY_A, ARRAY2_PERSON, ARRAY_PERSON),
        arguments(ARRAY_A, ARRAY2_NOTHING, ARRAY_NOTHING),
        arguments(ARRAY_A, ARRAY2_A, ARRAY_A),
        arguments(ARRAY_A, ARRAY2_B, ARRAY_B),

        arguments(ARRAY2_A, BOOL, null),
        arguments(ARRAY2_A, STRING, null),
        arguments(ARRAY2_A, BLOB, null),
        arguments(ARRAY2_A, PERSON, null),
        arguments(ARRAY2_A, NOTHING, NOTHING),
        arguments(ARRAY2_A, A, null),
        arguments(ARRAY2_A, B, null),

        arguments(ARRAY2_A, ARRAY_BOOL, null),
        arguments(ARRAY2_A, ARRAY_STRING, null),
        arguments(ARRAY2_A, ARRAY_BLOB, null),
        arguments(ARRAY2_A, ARRAY_PERSON, null),
        arguments(ARRAY2_A, ARRAY_NOTHING, NOTHING),
        arguments(ARRAY2_A, ARRAY_A, null),
        arguments(ARRAY2_A, ARRAY_B, null),

        arguments(ARRAY2_A, ARRAY2_BOOL, BOOL),
        arguments(ARRAY2_A, ARRAY2_STRING, STRING),
        arguments(ARRAY2_A, ARRAY2_BLOB, BLOB),
        arguments(ARRAY2_A, ARRAY2_PERSON, PERSON),
        arguments(ARRAY2_A, ARRAY2_NOTHING, NOTHING),
        arguments(ARRAY2_A, ARRAY2_A, A),
        arguments(ARRAY2_A, ARRAY2_B, B));
  }

  @ParameterizedTest
  @MethodSource("elemType_test_data")
  public void elemType(ArrayType type, Type expected) {
    assertThat(type.elemType())
        .isEqualTo(expected);
  }

  public static List<Arguments> elemType_test_data() {
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
  public void equality() {
    new EqualsTester()
        .addEqualityGroup(
            generic("A"),
            generic("A"))
        .addEqualityGroup(
            generic("B"),
            generic("B"))
        .addEqualityGroup(
            blob(),
            blob())
        .addEqualityGroup(
            bool(),
            bool())
        .addEqualityGroup(
            nothing(),
            nothing())
        .addEqualityGroup(
            string(),
            string())
        .addEqualityGroup(
            struct("MyStruct", FAKE_LOCATION, list()),
            struct("MyStruct", FAKE_LOCATION, list()))
        .addEqualityGroup(
            struct("MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))),
            struct("MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))))
        .addEqualityGroup(
            struct("MyStruct2", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))),
            struct("MyStruct2", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))))
        .addEqualityGroup(
            struct("MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field2", LOCATION))),
            struct("MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field2", LOCATION))))
        .addEqualityGroup(
            array(generic("A")),
            array(generic("A")))
        .addEqualityGroup(
            array(generic("B")),
            array(generic("B")))
        .addEqualityGroup(
            array(blob()),
            array(blob()))
        .addEqualityGroup(
            array(bool()),
            array(bool()))
        .addEqualityGroup(
            array(nothing()),
            array(nothing()))
        .addEqualityGroup(
            array(string()),
            array(string()))
        .addEqualityGroup(
            array(struct("MyStruct", FAKE_LOCATION, list())),
            array(struct("MyStruct", FAKE_LOCATION, list())))
        .addEqualityGroup(
            array(struct("MyStruct", FAKE_LOCATION,
                list(new Field(0, string(), "field", LOCATION)))),
            array(struct("MyStruct", FAKE_LOCATION,
                list(new Field(0, string(), "field", LOCATION)))))
        .addEqualityGroup(
            array(struct("MyStruct2", FAKE_LOCATION,
                list(new Field(0, string(), "field", LOCATION)))),
            array(struct("MyStruct2", FAKE_LOCATION,
                list(new Field(0, string(), "field", LOCATION)))))
        .addEqualityGroup(
            array(struct("MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field2", LOCATION)))),
            array(struct("MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field2", LOCATION)))))
        .addEqualityGroup(
            array(array(generic("A"))),
            array(array(generic("A"))))
        .addEqualityGroup(
            array(array(generic("B"))),
            array(array(generic("B"))))
        .addEqualityGroup(
            array(array(blob())),
            array(array(blob())))
        .addEqualityGroup(
            array(array(bool())),
            array(array(bool())))
        .addEqualityGroup(
            array(array(nothing())),
            array(array(nothing())))
        .addEqualityGroup(
            array(array(string())),
            array(array(string())))
        .addEqualityGroup(
            array(array(struct("MyStruct", FAKE_LOCATION, list()))),
            array(array(struct("MyStruct", FAKE_LOCATION, list()))))
        .addEqualityGroup(
            array(array(struct(
                "MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))))),
            array(array(struct(
                "MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))))))
        .addEqualityGroup(
            array(array(struct(
                "MyStruct2", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))))),
            array(array(struct(
                "MyStruct2", FAKE_LOCATION, list(new Field(0, string(), "field", LOCATION))))))
        .addEqualityGroup(
            array(array(struct(
                "MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field2", LOCATION))))),
            array(array(struct(
                "MyStruct", FAKE_LOCATION, list(new Field(0, string(), "field2", LOCATION))))))
        .testEquals();
  }
}
