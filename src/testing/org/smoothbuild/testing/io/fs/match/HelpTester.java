package org.smoothbuild.testing.io.fs.match;

import static com.google.common.base.CharMatcher.JAVA_LETTER;

import com.google.common.collect.ImmutableList;

public class HelpTester {

  public static final ImmutableList<String> ALL = ImmutableList.of("a", "b", "c", "aa", "ab", "ac",
      "ba", "bb", "bc", "ca", "cb", "cc");
  public static final ImmutableList<String> ALL_WITH_EMPTY = ImmutableList.of("", "a", "b", "c",
      "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc");
  public static final ImmutableList<String> ALL_DOUBLE_STARS = allDoubleStars();

  public static boolean endsWithThreeLetters(String pattern) {
    boolean isLongEnough = 3 <= pattern.length();
    return isLongEnough && JAVA_LETTER.matchesAllOf(pattern.substring(pattern.length() - 3));
  }

  private static ImmutableList<String> allDoubleStars() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    builder.add("");
    builder.addAll(ALL);
    for (String a : ALL) {
      for (String b : ALL) {
        builder.add(a + "/" + b);
      }
    }
    return builder.build();
  }
}
