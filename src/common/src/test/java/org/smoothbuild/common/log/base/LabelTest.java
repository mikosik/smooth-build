package org.smoothbuild.common.log.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;

public class LabelTest {
  @ParameterizedTest
  @MethodSource("illegal_labels")
  void factory_method_fails_for_illegal_label(String label) {
    assertCall(() -> label(label)).throwsException(IllegalArgumentException.class);
  }

  @Nested
  class _append {
    @Test
    void concatenates_part() {
      var label = label(":name");
      assertThat(label.append(":other")).isEqualTo(label(":name:other"));
    }

    @ParameterizedTest
    @MethodSource("org.smoothbuild.common.log.base.LabelTest#illegal_labels")
    void fails_when_part_is_illegal_label(String part) {
      var label = label(":name");
      assertCall(() -> label.append(part)).throwsException(IllegalArgumentException.class);
    }
  }

  public static List<Arguments> illegal_labels() {
    return list(
        arguments(""),
        arguments(" "),
        arguments(":"),
        arguments("!"),
        arguments("."),
        arguments(","),
        arguments("a"),
        arguments("a "),
        arguments(" b"),
        arguments("a b"),
        arguments("text"),
        arguments("text:"),
        arguments(":text:"),
        arguments("::"),
        arguments("::text"),
        arguments("text::"),
        arguments("::text::"),
        arguments(":text::text"));
  }

  @ParameterizedTest
  @MethodSource
  void startsWith(Label label, Label prefix, boolean expected) {
    assertThat(label.startsWith(prefix)).isEqualTo(expected);
  }

  public static List<Arguments> startsWith() {
    return list(
        arguments(label(":a"), label(":a"), true),
        arguments(label(":a"), label(":b"), false),
        arguments(label(":a"), label(":a:b"), false),
        arguments(label(":a:b"), label(":a"), true),
        arguments(label(":a:b"), label(":b"), false),
        arguments(label(":a:b"), label(":a:b"), true),
        arguments(label(":a:b"), label(":a:c"), false),
        arguments(label(":a:b"), label(":a:b:c"), false));
  }

  @Nested
  class _toString {
    @Test
    void with_one_part() {
      var label = label(":name");
      assertThat(label.toString()).isEqualTo(":name");
    }

    @Test
    void with_two_parts() {
      var label = label(":name").append(":second");
      assertThat(label.toString()).isEqualTo(":name:second");
    }
  }
}
