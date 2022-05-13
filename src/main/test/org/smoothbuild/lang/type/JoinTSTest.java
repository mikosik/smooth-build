package org.smoothbuild.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

import java.util.List;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.type.TestingTS;

@TestInstance(PER_CLASS)
public class JoinTSTest extends TestingTS {
  @ParameterizedTest
  @MethodSource("join_of_test_cases")
  public void join_of(TypeS a, TypeS b, TypeS expected) {
    assertThat(join(a, b))
        .isEqualTo(expected);
    assertThat(join(b, a))
        .isEqualTo(expected);
  }

  public List<Arguments> join_of_test_cases() {
    return List.of(
        Arguments.of(nothing(), any(), any()),
        Arguments.of(nothing(), string(), string()),
        Arguments.of(nothing(), array(string()), array(string())),
        Arguments.of(nothing(), joinStringBool(), joinStringBool()),
        Arguments.of(nothing(), nothing(), nothing()),

        Arguments.of(string(), any(), any()),
        Arguments.of(string(), array(string()), join(string(), array(string()))),
        Arguments.of(string(), bool(), joinStringBool()),
        Arguments.of(string(), string(), string()),

        Arguments.of(array(string()), any(), any()),
        Arguments.of(array(string()), array(string()), array(string())),

        Arguments.of(joinStringBool(), any(), any()),
        Arguments.of(joinStringBool(), bool(), joinStringBool()),
        Arguments.of(joinStringBool(), string(), joinStringBool()),

        Arguments.of(any(), any(), any())
    );
  }

  public TypeS joinStringBool() {
    return join(string(), bool());
  }
}
