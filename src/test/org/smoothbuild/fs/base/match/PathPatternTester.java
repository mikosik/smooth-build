package org.smoothbuild.fs.base.match;

import static java.lang.Character.isLetter;
import static org.smoothbuild.fs.base.match.Constants.SINGLE_STAR;

import com.google.common.base.Function;

public class PathPatternTester {

  /**
   * Generates all patterns containing 'size' or less elements and pass it to
   * given 'consumer'.
   * 
   * Element is either "**" wildcard, "*" wildcard, one of the letters {a, b, b}
   * or separator "/" (which is not taken into account when calculating pattern
   * size).
   */
  public static void generatePatterns(int i, Function<String, Void> consumer) {
    generatePatterns("", i, consumer);
  }

  private static void generatePatterns(String pattern, int size, Function<String, Void> consumer) {
    if (size == 0) {
      consumer.apply(pattern);
    } else {
      if (!endsWithThreeCharacters(pattern)) {
        generatePatterns(pattern + "a", size - 1, consumer);

        /*
         * To reduce number of generated patterns by merging equivalent patterns
         * (in terms of PathMatcher). This is done by making sure that each
         * generated pattern, the first occurrence of a letter is letter 'a' and
         * first occurrence of a letter different than 'a' is letter 'b'. This
         * way pattern like "bcb*aa" won't be generated as it is equivalent to
         * generated "aba*cc".
         */
        if (pattern.contains("a")) {
          generatePatterns(pattern + "b", size - 1, consumer);
          if (pattern.contains("b")) {
            generatePatterns(pattern + "c", size - 1, consumer);
          }
        }
      }
      if (pattern.length() != 0 && !pattern.endsWith("/")) {
        // slash "/" is not counted as element
        generatePatterns(pattern + "/", size, consumer);
      }

      if (!pattern.endsWith(SINGLE_STAR)) {
        generatePatterns(pattern + SINGLE_STAR, size - 1, consumer);
      }

      if (!pattern.endsWith("/")) {
        String start = pattern.isEmpty() ? "" : "/";
        String end = size == 1 ? "" : "/";
        generatePatterns(pattern + start + "**" + end, size - 1, consumer);
      }
    }
  }

  private static boolean endsWithThreeCharacters(String pattern) {
    int length = pattern.length();
    if (length < 3) {
      return false;
    }
    return (3 <= length) && isLetterAt(pattern, length - 1) && isLetterAt(pattern, length - 2)
        && isLetterAt(pattern, length - 3);
  }

  private static boolean isLetterAt(String pattern, int index) {
    return isLetter(pattern.charAt(index));
  }
}
