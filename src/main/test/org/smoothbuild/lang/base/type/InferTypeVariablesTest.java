package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.lang.base.type.InferTypeVariables.inferTypeVariables;
import static org.smoothbuild.lang.base.type.TestingTypes.A;
import static org.smoothbuild.lang.base.type.TestingTypes.ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY2_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_A;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_ANY;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_B;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.ARRAY_STRING;
import static org.smoothbuild.lang.base.type.TestingTypes.B;
import static org.smoothbuild.lang.base.type.TestingTypes.NOTHING;
import static org.smoothbuild.lang.base.type.TestingTypes.PERSON;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.lang.base.type.constraint.TestingConstraints.constraints;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.lang.base.type.constraint.Constraints;

public class InferTypeVariablesTest {
  @ParameterizedTest
  @MethodSource("inferTypeVariables_test_data")
  public void infer_type_variables(List<Type> types, List<Type> actualTypes,
      Constraints expected) {
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
            Constraints.empty()),

        // A <- Any
        arguments(
            list(A),
            list(ANY),
            constraints(A, ANY)),
        arguments(
            list(A),
            list(ARRAY_ANY),
            constraints(A, ARRAY_ANY)),
        arguments(
            list(A),
            list(ARRAY2_STRING),
            constraints(A, ARRAY2_STRING)),

        arguments(
            list(ARRAY_A),
            list(STRING),
            Constraints.empty()),
        arguments(
            list(ARRAY_A),
            list(ARRAY_STRING),
            constraints(A, STRING)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_STRING),
            constraints(A, ARRAY_STRING)),

        arguments(
            list(ARRAY2_A),
            list(STRING),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_STRING),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_STRING),
            constraints(A, STRING)),

        // A <- String
        arguments(
            list(A),
            list(STRING),
            constraints(A, STRING)),
        arguments(
            list(A),
            list(ARRAY_STRING),
            constraints(A, ARRAY_STRING)),
        arguments(
            list(A),
            list(ARRAY2_STRING),
            constraints(A, ARRAY2_STRING)),

        arguments(
            list(ARRAY_A),
            list(STRING),
            Constraints.empty()),
        arguments(
            list(ARRAY_A),
            list(ARRAY_STRING),
            constraints(A, STRING)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_STRING),
            constraints(A, ARRAY_STRING)),

        arguments(
            list(ARRAY2_A),
            list(STRING),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_STRING),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_STRING),
            constraints(A, STRING)),

        // A <- struct (Person)
        arguments(
            list(A),
            list(PERSON),
            constraints(A, PERSON)),
        arguments(
            list(A),
            list(ARRAY_PERSON),
            constraints(A, ARRAY_PERSON)),
        arguments(
            list(A),
            list(ARRAY2_PERSON),
            constraints(A, ARRAY2_PERSON)),

        arguments(
            list(ARRAY_A),
            list(PERSON),
            Constraints.empty()),
        arguments(
            list(ARRAY_A),
            list(ARRAY_PERSON),
            constraints(A, PERSON)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_PERSON),
            constraints(A, ARRAY_PERSON)),

        arguments(
            list(ARRAY2_A),
            list(PERSON),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_PERSON),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_PERSON),
            constraints(A, PERSON)),

        // A <- Nothing

        arguments(
            list(A),
            list(NOTHING),
            constraints(A, NOTHING)),
        arguments(
            list(A),
            list(ARRAY_NOTHING),
            constraints(A, ARRAY_NOTHING)),
        arguments(
            list(A),
            list(ARRAY2_NOTHING),
            constraints(A, ARRAY2_NOTHING)),

        arguments(
            list(ARRAY_A),
            list(NOTHING),
            constraints(A, NOTHING)),
        arguments(
            list(ARRAY_A),
            list(ARRAY_NOTHING),
            constraints(A, NOTHING)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_NOTHING),
            constraints(A, ARRAY_NOTHING)),

        arguments(
            list(ARRAY2_A),
            list(NOTHING),
            constraints(A, NOTHING)),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_NOTHING),
            constraints(A, NOTHING)),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_NOTHING),
            constraints(A, NOTHING)),

        // A <- B

        arguments(
            list(A),
            list(B),
            constraints(A, B)),
        arguments(
            list(A),
            list(ARRAY_B),
            constraints(A, ARRAY_B)),
        arguments(
            list(A),
            list(ARRAY2_B),
            constraints(A, ARRAY2_B)),

        arguments(
            list(ARRAY_A),
            list(B),
            Constraints.empty()),
        arguments(
            list(ARRAY_A),
            list(ARRAY_B),
            constraints(A, B)),
        arguments(
            list(ARRAY_A),
            list(ARRAY2_B),
            constraints(A, ARRAY_B)),

        arguments(
            list(ARRAY2_A),
            list(B),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY_B),
            Constraints.empty()),
        arguments(
            list(ARRAY2_A),
            list(ARRAY2_B),
            constraints(A, B)),

        // A <- Nothing, String; with conversions

        arguments(
            list(A, A),
            list(NOTHING, STRING),
            constraints(A, STRING)),
        arguments(
            list(A, ARRAY_A),
            list(STRING, ARRAY_NOTHING),
            constraints(A, STRING)),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_STRING),
            constraints(A, STRING)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_STRING, ARRAY_NOTHING),
            constraints(A, STRING)),
        arguments(
            list(A, A),
            list(ARRAY_STRING, NOTHING),
            constraints(A, ARRAY_STRING)),

        // A <- Nothing, String; with conversions

        arguments(
            list(A, A),
            list(NOTHING, PERSON),
            constraints(A, PERSON)),
        arguments(
            list(A, ARRAY_A),
            list(PERSON, ARRAY_NOTHING),
            constraints(A, PERSON)),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_PERSON),
            constraints(A, PERSON)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_PERSON, ARRAY_NOTHING),
            constraints(A, PERSON)),
        arguments(
            list(A, A),
            list(ARRAY_PERSON, NOTHING),
            constraints(A, ARRAY_PERSON)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_NOTHING, ARRAY2_STRING),
            constraints(A, ARRAY_STRING)),

        // A <- Nothing, A; with conversions

        arguments(
            list(A, A),
            list(NOTHING, A),
            constraints(A, A)),
        arguments(
            list(A, ARRAY_A),
            list(A, ARRAY_NOTHING),
            constraints(A, A)),
        arguments(
            list(A, ARRAY_A),
            list(NOTHING, ARRAY_A),
            constraints(A, A)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_A, ARRAY_NOTHING),
            constraints(A, A)),
        arguments(
            list(A, A),
            list(ARRAY_A, NOTHING),
            constraints(A, ARRAY_A)),
        arguments(
            list(ARRAY_A, ARRAY_A),
            list(ARRAY_NOTHING, ARRAY2_A),
            constraints(A, ARRAY_A)));
  }
}
