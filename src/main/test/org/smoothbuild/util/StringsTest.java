package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.escaped;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;
import static org.smoothbuild.util.Strings.stringToOptionalString;
import static org.smoothbuild.util.Strings.unescaped;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.UnescapingFailedExc.illegalEscapeSeqException;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.collect.ImmutableMap;

public class StringsTest {
  @Nested
  @DisplayName("unline()")
  class Unline {
    @Test
    public void zero_lines_gives_empty_string() {
      assertThat(unlines())
          .isEqualTo("");
    }

    @Test
    public void one_line_gives_unchanged_line() {
      assertThat(unlines("abc"))
          .isEqualTo("abc");
    }

    @Test
    public void more_lines() {
      assertThat(unlines("abc", "def", "ghi"))
          .isEqualTo("abc\ndef\nghi");
    }

    @Test
    public void does_not_change_new_lines() {
      assertThat(unlines("abc\n123"))
          .isEqualTo("abc\n123");
    }
  }

  @Nested
  @DisplayName("escapedAndLimitedWithEllipsis()")
  class EscapedAndLimitedWithEllipsis {
    @Test
    public void does_not_change_string_which_length_is_below_limit() {
      assertThat(escapedAndLimitedWithEllipsis("12345678", 10))
          .isEqualTo("\"12345678\"");
    }

    @Test
    public void adds_ellipsis_when_quoted_string_exceeds_limit() {
      assertThat(escapedAndLimitedWithEllipsis("123456789", 10))
          .isEqualTo("\"12345\"...");
    }

    @Test
    public void adds_ellipsis_when_quoted_string_with_specials_exceeds_limit() {
      assertThat(escapedAndLimitedWithEllipsis("12345678\n", 10))
          .isEqualTo("\"12345\"...");
    }
  }

  @Nested
  @DisplayName("escape()")
  class Escape {
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "a", "ab", "abc", "abcd"})
    public void does_not_change(String string) {
      assertThat(escaped(string))
          .isEqualTo(string);
    }

    @ParameterizedTest
    @ArgumentsSource(EscapeArguments.class)
    public void escapes(String unescaped, String escaped) {
      assertThat(escaped(unescaped))
          .isEqualTo(escaped);
    }
  }

  @Nested
  @DisplayName("unescape()")
  class Unescape {
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "a", "ab", "abc", "abcd"})
    public void does_not_change(String string) {
      assertThat(unescaped(string))
          .isEqualTo(string);
    }

    @ParameterizedTest
    @ArgumentsSource(EscapeArguments.class)
    public void unescapes(String unescaped, String escaped) {
      assertThat(unescaped(escaped))
          .isEqualTo(unescaped);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\", "abc\\", "\\a", "\\ "})
    public void fails_when_escape_code_is_missing(String string) {
      assertCall(() -> unescaped(string))
          .throwsException(UnescapingFailedExc.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\x", "abc\\x"})
    public void fails_when_escape_code_is_illegal(String string) {
      assertCall(() -> unescaped(string))
          .throwsException(UnescapingFailedExc.class);
    }

    @Test
    public void exception_points_to_illegal_escape_code() {
      assertCall(() -> unescaped("abc\\x"))
          .throwsException(illegalEscapeSeqException(4));
    }
  }

  static class EscapeArguments implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return createTestData(escapeMapping()).entrySet()
          .stream()
          .map(e -> Arguments.of(e.getKey(), e.getValue()));
    }
  }

  private static HashMap<String, String> createTestData(ImmutableMap<String, String> mappings) {
    HashMap<String, String> conversionMap = new HashMap<>();
    for (Entry<String, String> entry1 : mappings.entrySet()) {
      for (Entry<String, String> entry2 : mappings.entrySet()) {
        for (Entry<String, String> entry3 : mappings.entrySet()) {
          conversionMap.put(
              entry1.getKey() + entry2.getKey() + entry3.getKey(),
              entry1.getValue() + entry2.getValue() + entry3.getValue());
        }
      }
    }
    return conversionMap;
  }

  private static ImmutableMap<String, String> escapeMapping() {
    return ImmutableMap.<String, String>builder()
        .put("\t", "\\t")
        .put("\b", "\\b")
        .put("\n", "\\n")
        .put("\r", "\\r")
        .put("\f", "\\f")
        .put("\"", "\\\"")
        .put("\\", "\\\\")
        .put("", "")
        .build();
  }

  @Nested
  class _string_to_optional_string {
    @Test
    public void empty_string_converts_to_optional_empty() {
      assertThat(stringToOptionalString(""))
          .isEqualTo(Optional.empty());
    }
    @Test
    public void non_empty_string_converts_to_optional_of() {
      assertThat(stringToOptionalString("abc"))
          .isEqualTo(Optional.of("abc"));
    }
  }
}
