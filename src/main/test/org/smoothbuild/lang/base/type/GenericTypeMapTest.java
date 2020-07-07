package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.TestingTypes.array2A;
import static org.smoothbuild.lang.base.type.TestingTypes.array2B;
import static org.smoothbuild.lang.base.type.TestingTypes.array2Nothing;
import static org.smoothbuild.lang.base.type.TestingTypes.array2Person;
import static org.smoothbuild.lang.base.type.TestingTypes.array2String;
import static org.smoothbuild.lang.base.type.TestingTypes.arrayA;
import static org.smoothbuild.lang.base.type.TestingTypes.arrayB;
import static org.smoothbuild.lang.base.type.TestingTypes.arrayNothing;
import static org.smoothbuild.lang.base.type.TestingTypes.arrayPerson;
import static org.smoothbuild.lang.base.type.TestingTypes.arrayString;
import static org.smoothbuild.lang.base.type.TestingTypes.b;
import static org.smoothbuild.lang.base.type.TestingTypes.blob;
import static org.smoothbuild.lang.base.type.TestingTypes.nothing;
import static org.smoothbuild.lang.base.type.TestingTypes.person;
import static org.smoothbuild.lang.base.type.TestingTypes.string;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class GenericTypeMapTest {
  @ParameterizedTest
  @MethodSource("inferMapping_test_data")
  public void inferMapping(List<IType> types, List<IType> actualTypes, IType type, IType expected) {
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
            list(string),
            list(string),
            blob, blob),

        // a <- string
        arguments(
            list(a),
            list(string),
            a, string),
        arguments(
            list(a),
            list(arrayString),
            a, arrayString),
        arguments(
            list(a),
            list(array2String),
            a, array2String),

        arguments(
            list(arrayA),
            list(string),
            null, null),
        arguments(
            list(arrayA),
            list(arrayString),
            a, string),
        arguments(
            list(arrayA),
            list(array2String),
            a, arrayString),

        arguments(
            list(array2A),
            list(string),
            null, null),
        arguments(
            list(array2A),
            list(arrayString),
            null, null),
        arguments(
            list(array2A),
            list(array2String),
            a, string),

        // a <- struct (Person)
        arguments(
            list(a),
            list(person),
            a, person),
        arguments(
            list(a),
            list(arrayPerson),
            a, arrayPerson),
        arguments(
            list(a),
            list(array2Person),
            a, array2Person),

        arguments(
            list(arrayA),
            list(person),
            null, null),
        arguments(
            list(arrayA),
            list(arrayPerson),
            a, person),
        arguments(
            list(arrayA),
            list(array2Person),
            a, arrayPerson),

        arguments(
            list(array2A),
            list(person),
            null, null),
        arguments(
            list(array2A),
            list(arrayPerson),
            null, null),
        arguments(
            list(array2A),
            list(array2Person),
            a, person),

        // a <- Nothing

        arguments(
            list(a),
            list(nothing),
            a, nothing),
        arguments(
            list(a),
            list(arrayNothing),
            a, arrayNothing),
        arguments(
            list(a),
            list(array2Nothing),
            a, array2Nothing),

        arguments(
            list(arrayA),
            list(nothing),
            a, nothing),
        arguments(
            list(arrayA),
            list(arrayNothing),
            a, nothing),
        arguments(
            list(arrayA),
            list(array2Nothing),
            a, arrayNothing),

        arguments(
            list(array2A),
            list(nothing),
            a, nothing),
        arguments(
            list(array2A),
            list(arrayNothing),
            a, nothing),
        arguments(
            list(array2A),
            list(array2Nothing),
            a, nothing),

        // a <- b

        arguments(
            list(a),
            list(b),
            a, b),
        arguments(
            list(a),
            list(arrayB),
            a, arrayB),
        arguments(
            list(a),
            list(array2B),
            a, array2B),

        arguments(
            list(arrayA),
            list(b),
            null, null),
        arguments(
            list(arrayA),
            list(arrayB),
            a, b),
        arguments(
            list(arrayA),
            list(array2B),
            a, arrayB),

        arguments(
            list(array2A),
            list(b),
            null, null),
        arguments(
            list(array2A),
            list(arrayB),
            null, null),
        arguments(
            list(array2A),
            list(array2B),
            a, b),

        // a <- String, struct (Person); with conversions

        arguments(
            list(a, a),
            list(person, string),
            a, string),
        arguments(
            list(a, arrayA),
            list(person, arrayString),
            a, string),
        arguments(
            list(a, arrayA),
            list(string, arrayPerson),
            a, string),

        // a <- Nothing, String; with conversions

        arguments(
            list(a, a),
            list(nothing, string),
            a, string),
        arguments(
            list(a, arrayA),
            list(string, arrayNothing),
            a, string),
        arguments(
            list(a, arrayA),
            list(nothing, arrayString),
            a, string),
        arguments(
            list(arrayA, arrayA),
            list(arrayString, arrayNothing),
            a, string),
        arguments(
            list(a, a),
            list(arrayString, nothing),
            a, arrayString),

        // a <- Nothing, String; with conversions

        arguments(
            list(a, a),
            list(nothing, person),
            a, person),
        arguments(
            list(a, arrayA),
            list(person, arrayNothing),
            a, person),
        arguments(
            list(a, arrayA),
            list(nothing, arrayPerson),
            a, person),
        arguments(
            list(arrayA, arrayA),
            list(arrayPerson, arrayNothing),
            a, person),
        arguments(
            list(a, a),
            list(arrayPerson, nothing),
            a, arrayPerson),
        arguments(
            list(arrayA, arrayA),
            list(arrayNothing, array2String),
            a, arrayString),

        // a <- Nothing, a; with conversions

        arguments(
            list(a, a),
            list(nothing, a),
            a, a),
        arguments(
            list(a, arrayA),
            list(a, arrayNothing),
            a, a),
        arguments(
            list(a, arrayA),
            list(nothing, arrayA),
            a, a),
        arguments(
            list(arrayA, arrayA),
            list(arrayA, arrayNothing),
            a, a),
        arguments(
            list(a, a),
            list(arrayA, nothing),
            a, arrayA),
        arguments(
            list(arrayA, arrayA),
            list(arrayNothing, array2A),
            a, arrayA));
  }
}
