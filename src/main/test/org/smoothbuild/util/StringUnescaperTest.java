package org.smoothbuild.util;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.run.Runners.expect;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;
import org.quackery.report.AssumeException;

import com.google.common.collect.ImmutableMap;

@RunWith(QuackeryRunner.class)
public class StringUnescaperTest {

  @Quackery
  public static Suite mainSuite() {
    return suite("Test StringUnescaper")
        .add(suite("unescapes escaped")
            .addAll(createConversionMap().entrySet().stream()
                .map(e -> unescapes(e.getKey(), e.getValue()))
                .collect(toList())))
        .add(suite("preserves non escaped characters")
            .add(escapingDoesntChange(""))
            .add(escapingDoesntChange(" "))
            .add(escapingDoesntChange("  "))
            .add(escapingDoesntChange("a"))
            .add(escapingDoesntChange("ab"))
            .add(escapingDoesntChange("abc"))
            .add(escapingDoesntChange("abcd")))
        .add(suite("fails when escaping")
            .add(failsUnescaping("\\", 0))
            .add(failsUnescaping("abc\\", 3))
            .add(failsUnescaping("\\a", 1))
            .add(failsUnescaping("\\ ", 1)));
  }

  private static HashMap<String, String> createConversionMap() {
    ImmutableMap<String, String> mappings = ImmutableMap.<String, String>builder()
        .put("\\t", "\t")
        .put("\\b", "\b")
        .put("\\n", "\n")
        .put("\\r", "\r")
        .put("\\f", "\f")
        .put("\\\"", "\"")
        .put("\\\\", "\\")
        .put("", "")
        .build();

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

  private static Case escapingDoesntChange(String escaped) {
    return unescapes(escaped, escaped);
  }

  private static Case unescapes(String escaped, String unescaped) {
    return newCase(format("[%s] should be unescaped", escaped),
        () -> assertEquals(unescaped, unescaped(escaped)));
  }

  private static Test failsUnescaping(String escaped, int index) {
    return suite(escaped)
        .add(failsWithException(escaped))
        .add(failsAtIndex(escaped, index));
  }

  private static Test failsWithException(String escaped) {
    return expect(UnescapingFailedException.class,
        newCase(format("Unescaping [%s] should fail", escaped), () -> unescaped(escaped)));
  }

  private static Test failsAtIndex(String escaped, int index) {
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
