package org.smoothbuild.common;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.Strings.escaped;
import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.Strings.limitedWithEllipsis;
import static org.smoothbuild.common.Strings.unescaped;
import static org.smoothbuild.common.Strings.unlines;
import static org.smoothbuild.common.UnescapeFailedException.illegalEscapeSeqException;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

public class StringsTest {
  @Nested
  class _indent {
    @Test
    public void empty_string() {
      assertThat(indent("")).isEqualTo("");
    }

    @Test
    public void string_with_one_separator() {
      assertThat(indent("\n")).isEqualTo("\n");
    }

    @Test
    public void string_with_separators_only() {
      assertThat(indent("\n\n")).isEqualTo("\n\n");
    }

    @Test
    public void string_with_one_line() {
      assertThat(indent("abc")).isEqualTo("  abc");
    }

    @Test
    public void many_orphaned_separators_at_the_beginning() {
      assertThat(indent("\n\n\nabc")).isEqualTo("\n\n\n  abc");
    }

    @Test
    public void many_orphaned_separators_at_the_end() {
      assertThat(indent("abc\n\n\n")).isEqualTo("  abc\n\n\n");
    }

    @Test
    public void many_orphaned_separators_in_the_middle() {
      assertThat(indent("abc\n\n\ndef")).isEqualTo("  abc\n\n\n  def");
    }

    @Test
    public void blank_string() {
      assertThat(indent(" ")).isEqualTo("   ");
    }

    @Test
    public void blank_strings_separated() {
      assertThat(indent(" \n ")).isEqualTo("   \n   ");
    }

    @Test
    public void string_with_two_lines() {
      assertThat(indent("abc\ndef")).isEqualTo("  abc\n  def");
    }

    @Test
    public void string_with_three_lines() {
      assertThat(indent("abc\ndef\nghi")).isEqualTo("  abc\n  def\n  ghi");
    }
  }

  @Nested
  class _unline {
    @Test
    public void zero_lines_gives_empty_string() {
      assertThat(unlines()).isEqualTo("");
    }

    @Test
    public void one_line_gives_unchanged_line() {
      assertThat(unlines("abc")).isEqualTo("abc");
    }

    @Test
    public void more_lines() {
      assertThat(unlines("abc", "def", "ghi")).isEqualTo("abc\ndef\nghi");
    }

    @Test
    public void does_not_change_new_lines() {
      assertThat(unlines("abc\n123")).isEqualTo("abc\n123");
    }
  }

  @Nested
  class _limited_with_ellipsis {
    @Test
    public void does_not_change_string_which_length_is_below_limit() {
      assertThat(limitedWithEllipsis("1234567890", 10)).isEqualTo("1234567890");
    }

    @Test
    public void adds_ellipsis_when_quoted_string_exceeds_limit() {
      assertThat(limitedWithEllipsis("12345678901", 10)).isEqualTo("1234567...");
    }
  }

  @Nested
  class _escape {
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "a", "ab", "abc", "abcd"})
    public void does_not_change(String string) {
      assertThat(escaped(string)).isEqualTo(string);
    }

    @ParameterizedTest
    @ArgumentsSource(EscapeArgumentsProvider.class)
    public void escapes(String unescaped, String escaped) {
      assertThat(escaped(unescaped)).isEqualTo(escaped);
    }
  }

  @Nested
  class _unescape {
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "a", "ab", "abc", "abcd"})
    public void does_not_change(String string) {
      assertThat(unescaped(string)).isEqualTo(string);
    }

    @ParameterizedTest
    @ArgumentsSource(EscapeArgumentsProvider.class)
    public void unescapes(String unescaped, String escaped) {
      assertThat(unescaped(escaped)).isEqualTo(unescaped);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\", "abc\\", "\\a", "\\ "})
    public void fails_when_escape_code_is_missing(String string) {
      assertCall(() -> unescaped(string)).throwsException(UnescapeFailedException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\\x", "abc\\x"})
    public void fails_when_escape_code_is_illegal(String string) {
      assertCall(() -> unescaped(string)).throwsException(UnescapeFailedException.class);
    }

    @Test
    public void exception_points_to_illegal_escape_code() {
      assertCall(() -> unescaped("abc\\x")).throwsException(illegalEscapeSeqException(4));
    }
  }

  static class EscapeArgumentsProvider implements ArgumentsProvider {
    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return createTestData(escapeMapping()).entrySet().stream()
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
}
