package org.smoothbuild.builtin.file.match.testing;

import static java.lang.Character.isLetter;
import static java.util.Collections.unmodifiableList;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;

public class HelpTester {

  public static final List<String> ALL = unmodifiableList(list("a", "b", "c", "aa", "ab", "ac",
      "ba", "bb", "bc", "ca", "cb", "cc"));
  public static final List<String> ALL_WITH_EMPTY = unmodifiableList(list("", "a", "b", "c",
      "aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc"));
  public static final List<String> ALL_DOUBLE_STARS = unmodifiableList(allDoubleStars());

  public static boolean endsWithThreeLetters(String pattern) {
    boolean isLongEnough = 3 <= pattern.length();
    return isLongEnough && containsOnlyLetters(pattern.substring(pattern.length() - 3));
  }

  private static boolean containsOnlyLetters(String string) {
    for (int i = 0; i < string.length(); i++) {
      if (!isLetter(string.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  private static List<String> allDoubleStars() {
    List<String> result = new ArrayList<>();
    result.add("");
    result.addAll(ALL);
    for (String a : ALL) {
      for (String b : ALL) {
        result.add(a + "/" + b);
      }
    }
    return result;
  }
}
