package org.smoothbuild.util;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.expect;
import static org.smoothbuild.util.Strings.escaped;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;
import static org.smoothbuild.util.Strings.unescaped;
import static org.smoothbuild.util.Strings.unlines;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;
import org.quackery.report.AssumeException;

import com.google.common.collect.ImmutableBiMap;

@RunWith(QuackeryRunner.class)
public class StringsTest {
  @Test
  public void unline_zero_lines_gives_empty_string() {
    when(unlines());
    thenReturned("");
  }

  @Test
  public void unline_one_line_gives_unchanged_line() {
    when(unlines("abc"));
    thenReturned("abc");
  }

  @Test
  public void unline_more_lines() {
    when(unlines(
        "abc",
        "def",
        "ghi"));
    thenReturned("abc\ndef\nghi");
  }

  @Test
  public void unline_doesnt_change_new_lines() {
    when(unlines("abc\n123"));
    thenReturned("abc\n123");
  }

  @Test
  public void escaped_with_ellipsis_doesnt_change_string_which_length_is_below_limit() {
    when(escapedAndLimitedWithEllipsis("12345678", 10));
    thenReturned("\"12345678\"");
  }

  @Test
  public void escaped_with_ellipsis_adds_ellipsis_when_quoted_string_exceeds_limit() {
    when(escapedAndLimitedWithEllipsis("123456789", 10));
    thenReturned("\"12345\"...");
  }

  @Test
  public void escaped_with_ellipsis_adds_ellipsis_when_quoted_string_with_specials_exceeds_limit() {
    when(escapedAndLimitedWithEllipsis("12345678\n", 10));
    thenReturned("\"12345\"...");
  }

  @Quackery
  public static Suite mainSuite() {
    return suite("Test Strings")
        .add(suite("unescapes escaped")
            .addAll(createTestDataForUnescaping().entrySet().stream()
                .map(e -> unescapes(e.getKey(), e.getValue()))
                .collect(toList())))
        .add(suite("preserves non escaped characters")
            .add(unescapingDoesntChange(""))
            .add(unescapingDoesntChange(" "))
            .add(unescapingDoesntChange("  "))
            .add(unescapingDoesntChange("a"))
            .add(unescapingDoesntChange("ab"))
            .add(unescapingDoesntChange("abc"))
            .add(unescapingDoesntChange("abcd")))
        .add(suite("fails when escaping")
            .add(failsUnescaping("\\", 0))
            .add(failsUnescaping("abc\\", 3))
            .add(failsUnescaping("\\a", 1))
            .add(failsUnescaping("\\ ", 1)))
        .add(suite("escapes escaped")
            .addAll(createTestDataForEscaping().entrySet().stream()
                .map(e -> escapes(e.getKey(), e.getValue()))
                .collect(toList())))
        .add(suite("preserves non escaped characters")
            .add(escapingDoesntChange(""))
            .add(escapingDoesntChange(" "))
            .add(escapingDoesntChange("  "))
            .add(escapingDoesntChange("a"))
            .add(escapingDoesntChange("ab"))
            .add(escapingDoesntChange("abc"))
            .add(escapingDoesntChange("abcd")));
  }

  private static HashMap<String, String> createTestDataForEscaping() {
    return createTestData(escapeMapping());
  }

  private static HashMap<String, String> createTestDataForUnescaping() {
    return createTestData(unescapeMapping());
  }

  private static HashMap<String, String> createTestData(ImmutableBiMap<String, String> mappings) {
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

  private static ImmutableBiMap<String, String> unescapeMapping() {
    return escapeMapping().inverse();
  }

  private static ImmutableBiMap<String, String> escapeMapping() {
    return ImmutableBiMap.<String, String>builder()
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

  private static Case escapingDoesntChange(String escaped) {
    return escapes(escaped, escaped);
  }

  private static Case escapes(String string, String escaped) {
    return newCase(format("[%s] should be escaped as [%s]", string, escaped),
        () -> assertEquals(escaped, escaped(string)));
  }

  private static Case unescapingDoesntChange(String escaped) {
    return unescapes(escaped, escaped);
  }

  private static Case unescapes(String escaped, String unescaped) {
    return newCase(format("[%s] should be unescaped", escaped),
        () -> assertEquals(unescaped, unescaped(escaped)));
  }

  private static org.quackery.Test failsUnescaping(String escaped, int index) {
    return suite(escaped)
        .add(failsWithException(escaped))
        .add(failsAtIndex(escaped, index));
  }

  private static org.quackery.Test failsWithException(String escaped) {
    return expect(UnescapingFailedException.class,
        newCase(format("Unescaping [%s] should fail", escaped), () -> unescaped(escaped)));
  }

  private static org.quackery.Test failsAtIndex(String escaped, int index) {
    return newCase(format("at index %d", index), () -> {
      try {
        unescaped(escaped);
        throw new AssumeException();
      } catch (UnescapingFailedException e) {
        assertEquals(index, e.charIndex());
      } catch (RuntimeException e) {
        throw new AssumeException(e);
      }
    });
  }
}
