package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.BLOB;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GenericTypeMapTest {
  @ParameterizedTest
  @MethodSource("inferMapping_test_data")
  public void inferMapping(List<Type> types, List<Type> actualTypes, Type type, Type expected) {
    if (type == null) {
      assertCall(() -> GenericTypeMap
          .inferMapping(types, actualTypes))
          .throwsException(IllegalArgumentException.class);
    } else {
      assertThat(GenericTypeMap.inferMapping(types, actualTypes).applyTo(type))
          .isEqualTo(expected);
    }
  }

  public static List<Arguments> inferMapping_test_data() {
    return List.of(
        // concrete types
        arguments(
            list(STRING),
            list(STRING),
            BLOB, BLOB),

        // a <- string
        arguments(
            list(A),
            list(STRING),
            A, STRING),
        arguments(
            list(A),
            list(ARRAY_STRING),
            A, ARRAY_STRING),
        arguments(
            list(A),
            list(ARRAY2_STRING),
            A, ARRAY2_STRING),

        arguments(
            list(ARRAY_A),
            list(STRING),
            null, null),
        arguments(
            list(ARRAY_A),
            list(ARRAY_STRING),
            A, STRING),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_STRING),
            A, ARRAY_STRING),

        arguments(
            list(ARRAY2_A),
            list(STRING),
            null, null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_STRING),
            null, null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_STRING),
            A, STRING),

        // a <- struct (Person)
        arguments(
            list(A),
            list(PERSON),
            A, PERSON),
        arguments(
            list(A),
            list(ARRAY_PERSON),
            A, ARRAY_PERSON),
        arguments(
            list(A),
            list(ARRAY2_PERSON),
            A, ARRAY2_PERSON),

        arguments(
            list(ARRAY_A),
            list(PERSON),
            null, null),
        arguments(
            list(ARRAY_A),
            list(ARRAY_PERSON),
            A, PERSON),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_PERSON),
            A, ARRAY_PERSON),

        arguments(
            list(ARRAY2_A),
            list(PERSON),
            null, null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_PERSON),
            null, null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_PERSON),
            A, PERSON),

        // a <- Nothing

        arguments(
            list(A),
            list(NOTHING),
            A, NOTHING),
        arguments(
            list(A),
            list(ARRAY_NOTHING),
            A, ARRAY_NOTHING),
        arguments(
            list(A),
            list(ARRAY2_NOTHING),
            A, ARRAY2_NOTHING),

        arguments(
            list(ARRAY_A),
            list(NOTHING),
            A, NOTHING),
        arguments(
            list(ARRAY_A),
            list(ARRAY_NOTHING),
            A, NOTHING),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_NOTHING),
            A, ARRAY_NOTHING),

        arguments(
            list(ARRAY2_A),
            list(NOTHING),
            A, NOTHING),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_NOTHING),
            A, NOTHING),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_NOTHING),
            A, NOTHING),

        // a <- b

        arguments(
            list(A),
            list(B),
            A, B),
        arguments(
            list(A),
            list(ARRAY_B),
            A, ARRAY_B),
        arguments(
            list(A),
            list(ARRAY2_B),
            A, ARRAY2_B),

        arguments(
            list(ARRAY_A),
            list(B),
            null, null),
        arguments(
            list(ARRAY_A),
            list(ARRAY_B),
            A, B),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_B),
            A, ARRAY_B),

        arguments(
            list(ARRAY2_A),
            list(B),
            null, null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_B),
            null, null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_B),
            A, B),

        // a <- String, struct (Person); with conversions

        arguments(
            list(A, A),
            list(PERSON, STRING),
            A, STRING),
        arguments(
            list(A, ARRAY_A),
            list(PERSON, ARRAY_STRING),
            A, STRING),
        arguments(
            list(A, ARRAY_A),
            list(STRING, ARRAY_PERSON),
            A, STRING),

        // a <- Nothing, String; with conversions

        arguments(
            list(A, A),
            list(NOTHING, STRING),
            A, STRING),
        arguments(
            list(A, ARRAY_A),
            list(STRING, ARRAY_NOTHING),
            A, STRING),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_STRING),
            A, STRING),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_STRING, ARRAY_NOTHING),
            A, STRING),
        arguments(
            list(A, A),
            list(ARRAY_STRING, NOTHING),
            A, ARRAY_STRING),

        // a <- Nothing, String; with conversions

        arguments(
            list(A, A),
            list(NOTHING, PERSON),
            A, PERSON),
        arguments(
            list(A, ARRAY_A),
            list(PERSON, ARRAY_NOTHING),
            A, PERSON),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_PERSON),
            A, PERSON),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_PERSON, ARRAY_NOTHING),
            A, PERSON),
        arguments(
            list(A, A),
            list(ARRAY_PERSON, NOTHING),
            A, ARRAY_PERSON),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_NOTHING, ARRAY2_STRING),
            A, ARRAY_STRING),

        // a <- Nothing, a; with conversions

        arguments(
            list(A, A),
            list(NOTHING, A),
            A, A),
        arguments(
            list(A, ARRAY_A),
            list(A, ARRAY_NOTHING),
            A, A),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_A),
            A, A),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_A, ARRAY_NOTHING),
            A, A),
        arguments(
            list(A, A),
            list(ARRAY_A, NOTHING),
            A, ARRAY_A),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_NOTHING, ARRAY2_A),
            A, ARRAY_A));
  }
}
