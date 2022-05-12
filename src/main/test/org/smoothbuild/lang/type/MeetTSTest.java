package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.smoothbuild.lang.type.MeetTS.meet;

import java.util.List;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.type.TestingTS;

@TestInstance(PER_CLASS)
public class MeetTSTest extends TestingTS {
  @ParameterizedTest
  @MethodSource("meet_of_test_cases")
  public void meet_of(TypeS a, TypeS b, TypeS expected) {
    assertThat(meet(a, b))
        .isEqualTo(expected);
    assertThat(meet(b, a))
        .isEqualTo(expected);
  }

  public List<Arguments> meet_of_test_cases() {
    return List.of(
        Arguments.of(nothing(), any(), nothing()),
        Arguments.of(nothing(), string(), nothing()),
        Arguments.of(nothing(), array(string()), nothing()),
        Arguments.of(nothing(), meetStringBool(), nothing()),
        Arguments.of(nothing(), nothing(), nothing()),

        Arguments.of(string(), any(), string()),
        Arguments.of(string(), array(string()), meet(string(), array(string()))),
        Arguments.of(string(), bool(), meetStringBool()),
        Arguments.of(string(), string(), string()),

        Arguments.of(array(string()), any(), array(string())),
        Arguments.of(array(string()), array(string()), array(string())),

        Arguments.of(meetStringBool(), any(), meetStringBool()),
        Arguments.of(meetStringBool(), bool(), meetStringBool()),
        Arguments.of(meetStringBool(), string(), meetStringBool()),

        Arguments.of(any(), any(), any())
    );
  }

  public TypeS meetStringBool() {
    return meet(string(), bool());
  }
}
