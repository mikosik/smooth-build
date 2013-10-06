package org.smoothbuild.fs.base.match;

import static org.smoothbuild.fs.base.match.Constants.SINGLE_STAR;
import static org.smoothbuild.testing.fs.base.match.HelpTester.endsWithThreeLetters;

import com.google.common.base.Function;

public class PathPatternGenerator {

  /**
   * Generates all patterns containing 'size' or less elements and pass it to
   * given 'consumer'.
   * 
   * Element is either "**" wildcard, "*" wildcard, one of the letters {a, b, b}
   * or separator "/" (which is not taken into account when calculating pattern
   * size).
   */
  public static void generatePatterns(int size, Function<String, Void> consumer) {
    for (int i = 1; i <= size; i++) {
      generatePatterns("", i, consumer);
    }
  }

  private static void generatePatterns(String pattern, int size, Function<String, Void> consumer) {
    if (size == 0) {
      consumer.apply(pattern);
    } else {
      if (!endsWithThreeLetters(pattern)) {
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
}
