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
  @MethodSource
  void colon_is_forbidden_inside_part_string(String... parts) {
    assertCall(() -> label(parts))
        .throwsException(new IllegalArgumentException("Label part cannot contain `:`."));
  }

  public static List<Arguments> colon_is_forbidden_inside_part_string() {
    return list(
        arguments((Object) arrayOf(":")),
        arguments((Object) arrayOf(":part")),
        arguments((Object) arrayOf("part:")),
        arguments((Object) arrayOf(":part:")),
        arguments((Object) arrayOf("part:part")),
        arguments((Object) arrayOf("::")),
        arguments((Object) arrayOf("::part")),
        arguments((Object) arrayOf("part::")),
        arguments((Object) arrayOf("::part::")),
        arguments((Object) arrayOf("part::part")));
  }

  private static String[] arrayOf(String... strings) {
    return strings;
  }

  @ParameterizedTest
  @MethodSource
  void startsWith(Label label, Label prefix, boolean expected) {
    assertThat(label.startsWith(prefix)).isEqualTo(expected);
  }

  public static List<Arguments> startsWith() {
    return list(
        arguments(label(), label(), true),
        arguments(label(), label("a"), false),
        arguments(label("a"), label(), true),
        arguments(label("a"), label("a"), true),
        arguments(label("a"), label("b"), false),
        arguments(label("a"), label("a", "b"), false),
        arguments(label("a", "b"), label(), true),
        arguments(label("a", "b"), label("a"), true),
        arguments(label("a", "b"), label("b"), false),
        arguments(label("a", "b"), label("a", "b"), true),
        arguments(label("a", "b"), label("a", "c"), false),
        arguments(label("a", "b"), label("a", "b", "c"), false));
  }

  @Nested
  class _toString {
    @Test
    void with_zero_parts() {
      var label = label();
      assertThat(label.toString()).isEqualTo(":");
    }

    @Test
    void with_one_part() {
      var label = label("name");
      assertThat(label.toString()).isEqualTo(":name");
    }

    @Test
    void with_two_parts() {
      var label = label("name").append(label("second"));
      assertThat(label.toString()).isEqualTo(":name:second");
    }
  }
}
