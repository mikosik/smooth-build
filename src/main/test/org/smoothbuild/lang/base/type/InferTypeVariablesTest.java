package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.InferTypeVariables.inferTypeVariables;
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
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class InferTypeVariablesTest {
  @ParameterizedTest
  @MethodSource("inferTypeVariables_test_data")
  public void infer_type_variables(List<Type> types, List<Type> actualTypes,
      Map<TypeVariable, Type> expected) {
    if (expected == null) {
      assertCall(() -> inferTypeVariables(types, actualTypes))
          .throwsException(IllegalArgumentException.class);
    } else {
      assertThat(inferTypeVariables(types, actualTypes))
          .isEqualTo(expected);
    }
  }

  public static List<Arguments> inferTypeVariables_test_data() {
    return List.of(
        // monotype
        arguments(
            list(STRING),
            list(STRING),
            Map.of()),

        // a <- string
        arguments(
            list(A),
            list(STRING),
            Map.of(A, STRING)),
        arguments(
            list(A),
            list(ARRAY_STRING),
            Map.of(A, ARRAY_STRING)),
        arguments(
            list(A),
            list(ARRAY2_STRING),
            Map.of(A, ARRAY2_STRING)),

        arguments(
            list(ARRAY_A),
            list(STRING),
            null),
        arguments(
            list(ARRAY_A),
            list(ARRAY_STRING),
            Map.of(A, STRING)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_STRING),
            Map.of(A, ARRAY_STRING)),

        arguments(
            list(ARRAY2_A),
            list(STRING),
            null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_STRING),
            null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_STRING),
            Map.of(A, STRING)),

        // a <- struct (Person)
        arguments(
            list(A),
            list(PERSON),
            Map.of(A, PERSON)),
        arguments(
            list(A),
            list(ARRAY_PERSON),
            Map.of(A, ARRAY_PERSON)),
        arguments(
            list(A),
            list(ARRAY2_PERSON),
            Map.of(A, ARRAY2_PERSON)),

        arguments(
            list(ARRAY_A),
            list(PERSON),
            null),
        arguments(
            list(ARRAY_A),
            list(ARRAY_PERSON),
            Map.of(A, PERSON)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_PERSON),
            Map.of(A, ARRAY_PERSON)),

        arguments(
            list(ARRAY2_A),
            list(PERSON),
            null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_PERSON),
            null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_PERSON),
            Map.of(A, PERSON)),

        // a <- Nothing

        arguments(
            list(A),
            list(NOTHING),
            Map.of(A, NOTHING)),
        arguments(
            list(A),
            list(ARRAY_NOTHING),
            Map.of(A, ARRAY_NOTHING)),
        arguments(
            list(A),
            list(ARRAY2_NOTHING),
            Map.of(A, ARRAY2_NOTHING)),

        arguments(
            list(ARRAY_A),
            list(NOTHING),
            Map.of(A, NOTHING)),
        arguments(
            list(ARRAY_A),
            list(ARRAY_NOTHING),
            Map.of(A, NOTHING)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_NOTHING),
            Map.of(A, ARRAY_NOTHING)),

        arguments(
            list(ARRAY2_A),
            list(NOTHING),
            Map.of(A, NOTHING)),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_NOTHING),
            Map.of(A, NOTHING)),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_NOTHING),
            Map.of(A, NOTHING)),

        // a <- b

        arguments(
            list(A),
            list(B),
            Map.of(A, B)),
        arguments(
            list(A),
            list(ARRAY_B),
            Map.of(A, ARRAY_B)),
        arguments(
            list(A),
            list(ARRAY2_B),
            Map.of(A, ARRAY2_B)),

        arguments(
            list(ARRAY_A),
            list(B),
            null),
        arguments(
            list(ARRAY_A),
            list(ARRAY_B),
            Map.of(A, B)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_B),
            Map.of(A, ARRAY_B)),

        arguments(
            list(ARRAY2_A),
            list(B),
            null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_B),
            null),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_B),
            Map.of(A, B)),

        // a <- Nothing, String; with conversions

        arguments(
            list(A, A),
            list(NOTHING, STRING),
            Map.of(A, STRING)),
        arguments(
            list(A, ARRAY_A),
            list(STRING, ARRAY_NOTHING),
            Map.of(A, STRING)),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_STRING),
            Map.of(A, STRING)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_STRING, ARRAY_NOTHING),
            Map.of(A, STRING)),
        arguments(
            list(A, A),
            list(ARRAY_STRING, NOTHING),
            Map.of(A, ARRAY_STRING)),

        // a <- Nothing, String; with conversions

        arguments(
            list(A, A),
            list(NOTHING, PERSON),
            Map.of(A, PERSON)),
        arguments(
            list(A, ARRAY_A),
            list(PERSON, ARRAY_NOTHING),
            Map.of(A, PERSON)),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_PERSON),
            Map.of(A, PERSON)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_PERSON, ARRAY_NOTHING),
            Map.of(A, PERSON)),
        arguments(
            list(A, A),
            list(ARRAY_PERSON, NOTHING),
            Map.of(A, ARRAY_PERSON)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_NOTHING, ARRAY2_STRING),
            Map.of(A, ARRAY_STRING)),

        // a <- Nothing, a; with conversions

        arguments(
            list(A, A),
            list(NOTHING, A),
            Map.of(A, A)),
        arguments(
            list(A, ARRAY_A),
            list(A, ARRAY_NOTHING),
            Map.of(A, A)),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_A),
            Map.of(A, A)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_A, ARRAY_NOTHING),
            Map.of(A, A)),
        arguments(
            list(A, A),
            list(ARRAY_A, NOTHING),
            Map.of(A, ARRAY_A)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_NOTHING, ARRAY2_A),
            Map.of(A, ARRAY_A)));
  }
}
