package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LogLevelConverterTest {
  @ParameterizedTest
  @MethodSource("conversions")
  public void converter(String value, Level expectedLevel) {
    LogLevelConverter converter = new LogLevelConverter();
    assertThat(converter.convert(value))
        .isEqualTo(expectedLevel);
  }

  public static Stream<Arguments> conversions() {
    return Stream.of(
        arguments("f", FATAL),
        arguments("fatal", FATAL),
        arguments("e", ERROR),
        arguments("error", ERROR),
        arguments("w", WARNING),
        arguments("warning", WARNING),
        arguments("i", INFO),
        arguments("info", INFO)
    );
  }
}
