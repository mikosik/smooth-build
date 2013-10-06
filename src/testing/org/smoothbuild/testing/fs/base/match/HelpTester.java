package org.smoothbuild.testing.fs.base.match;

import static com.google.common.base.CharMatcher.JAVA_LETTER;

import com.google.common.collect.ImmutableList;

public class HelpTester {

  public static final ImmutableList<String> ALL = ImmutableList.of("a", "b", "c", "aa", "ab", "ac",
      "ba", "bb", "bc", "ca", "cb", "cc");
  public static final ImmutableList<String> ALL_WITH_EMPTY = ImmutableList.of("", "a", "b", "c",
      "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc");

  public static boolean endsWithThreeCharacters(String pattern) {
    boolean isLongEnough = 3 <= pattern.length();
    return isLongEnough && JAVA_LETTER.matchesAllOf(pattern.substring(pattern.length() - 3));
  }
}
