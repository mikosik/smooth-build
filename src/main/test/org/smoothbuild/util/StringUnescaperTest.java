package org.smoothbuild.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.Map;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Case.Body;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;

import com.google.common.collect.ImmutableMap;

@RunWith(QuackeryRunner.class)
public class StringUnescaperTest {
  @Quackery
  public static Suite preserves_non_escaped_characters() {
    return suite("preserves non escaped characters")
        .add(shouldUnescape(""))
        .add(shouldUnescape(" "))
        .add(shouldUnescape("  "))
        .add(shouldUnescape("a"))
        .add(shouldUnescape("ab"))
        .add(shouldUnescape("abc"))
        .add(shouldUnescape("abcd"));
  }

  @Quackery
  public static Suite fails_for_illegal_escapes() {
    return suite("fails for illegal escapes")
        .add(shouldFailUnescaping("\\", 0))
        .add(shouldFailUnescaping("abc\\", 3))
        .add(shouldFailUnescaping("\\a", 1))
        .add(shouldFailUnescaping("\\ ", 1));
  }

  @Quackery
  public static Suite unsecaping_combined() throws Exception {
    Suite suite = suite("three escaped in a row");
    ImmutableMap<String, String> mappings = ImmutableMap.<String, String> builder()
        .put("\\t", "\t")
        .put("\\b", "\b")
        .put("\\n", "\n")
        .put("\\r", "\r")
        .put("\\f", "\f")
        .put("\\\"", "\"")
        .put("\\\\", "\\")
        .build();

    for (Map.Entry<String, String> entry1 : mappings.entrySet()) {
      suite = suite.add(shouldUnescape(entry1.getKey(), entry1.getValue()));
      for (Map.Entry<String, String> entry2 : mappings.entrySet()) {
        suite = suite.add(shouldUnescape(
            entry1.getKey() + entry2.getKey(),
            entry1.getValue() + entry2.getValue()));
        for (Map.Entry<String, String> entry3 : mappings.entrySet()) {
          suite = suite.add(shouldUnescape(
              entry1.getKey() + entry2.getKey() + entry3.getKey(),
              entry1.getValue() + entry2.getValue() + entry3.getValue()));
        }
      }
    }
    return suite;
  }

  private static Case shouldUnescape(String escaped) {
    return shouldUnescape(escaped, escaped);
  }

  private static Case shouldUnescape(String escaped, String unescaped) {
    return newCase("[" + escaped + "] should be unescaped",
        () -> assertEquals(unescaped, unescaped(escaped)));
  }

  private static Case shouldFailUnescaping(String escaped, int index) {
    return newCase("Unescaping [" + escaped + "] should fail at " + index,
        new Body() {
          public void run() throws Throwable {
            try {
              unescaped(escaped);
              fail("exception should be thrown");
            } catch (UnescapingFailedException e) {
              assertEquals(index, e.charIndex());
            }
          }
        });
  }
}
