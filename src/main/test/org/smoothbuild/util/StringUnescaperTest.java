package org.smoothbuild.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.smoothbuild.util.StringUnescaper.unescaped;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class StringUnescaperTest {
  private static final String T = "\t";
  private static final String B = "\b";
  private static final String N = "\n";
  private static final String C = "\r";
  private static final String F = "\f";
  private static final String D = "\"";
  private static final String S = "\\";

  private static final String ET = S + "t";
  private static final String EB = S + "b";
  private static final String EN = S + "n";
  private static final String EC = S + "r";
  private static final String EF = S + "f";
  private static final String ED = S + D;
  private static final String ES = S + S;

  private static final Map<String, String> UNESCAPE = unescapeMap();

  @Test
  public void stringThatDoesNotNeedUnescapingAreNotChanged() {
    assertUnescapedResult("", "");
    assertUnescapedResult(" ", " ");
    assertUnescapedResult("  ", "  ");

    assertUnescapedResult("a", "a");
    assertUnescapedResult("ab", "ab");
    assertUnescapedResult("abc", "abc");
    assertUnescapedResult("abcd", "abcd");
  }

  @Test
  public void slashAtTheEndOfStringCannotBeUnescaped() throws Exception {
    assertUnescapingFails(S, 0);
    assertUnescapingFails("abc" + S, 3);
  }

  @Test
  public void incorrectUnescapeCodeCannotBeUnescaped() throws Exception {
    assertUnescapingFails(S + "x", 1);
    assertUnescapingFails(S + "a", 1);
    assertUnescapingFails(S + "w", 1);
    assertUnescapingFails(S + " ", 1);
  }

  private void assertUnescapingFails(String string, int index) {
    try {
      StringUnescaper.unescaped(string);
      fail("exception should be thrown");
    } catch (UnescapingFailedException e) {
      // expected
      assertEquals(index, e.charIndex());
    }
  }

  @Test
  public void unescapingSingleEscapedElement() throws Exception {
    for (Map.Entry<String, String> entry : UNESCAPE.entrySet()) {
      assertUnescapedResult(entry.getKey(), entry.getValue());
    }
  }

  @Test
  public void unescapingTwoEscapedElements() throws Exception {
    for (Map.Entry<String, String> entry1 : UNESCAPE.entrySet()) {
      for (Map.Entry<String, String> entry2 : UNESCAPE.entrySet()) {
        String escaped = entry1.getKey() + entry2.getKey();
        String unescaped = entry1.getValue() + entry2.getValue();
        assertUnescapedResult(escaped, unescaped);
      }
    }
  }

  @Test
  public void unescapingThreeEscapedElements() throws Exception {
    for (Map.Entry<String, String> entry1 : UNESCAPE.entrySet()) {
      for (Map.Entry<String, String> entry2 : UNESCAPE.entrySet()) {
        for (Map.Entry<String, String> entry3 : UNESCAPE.entrySet()) {
          String escaped = entry1.getKey() + entry2.getKey() + entry3.getKey();
          String unescaped = entry1.getValue() + entry2.getValue() + entry3.getValue();
          assertUnescapedResult(escaped, unescaped);
        }
      }
    }
  }

  private void assertUnescapedResult(String input, String expected) {
    assertEquals(expected, unescaped(input));
  }

  private static Map<String, String> unescapeMap() {
    Builder<String, String> builder = ImmutableMap.builder();
    builder.put(ET, T);
    builder.put(EB, B);
    builder.put(EN, N);
    builder.put(EC, C);
    builder.put(EF, F);
    builder.put(ED, D);
    builder.put(ES, S);
    return builder.build();
  }
}
