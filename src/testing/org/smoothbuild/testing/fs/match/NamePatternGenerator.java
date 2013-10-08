package org.smoothbuild.testing.fs.match;

import static org.smoothbuild.fs.match.Constants.SINGLE_STAR;
import static org.smoothbuild.testing.fs.match.HelpTester.endsWithThreeLetters;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

public class NamePatternGenerator {

  /**
   * Generates all name patterns containing 'size' or less elements and pass it
   * to given 'consumer'.
   * 
   * Element is either "*" wildcard or one of the letters {a, b, b}.
   */
  public static void generatePatterns(int maxSize, Function<String, Void> consumer) {
    for (int i = 1; i <= maxSize; i++) {
      generatePatternsImpl(i, consumer);
    }
  }

  @VisibleForTesting
  static void generatePatternsImpl(int size, Function<String, Void> consumer) {
    generatePatterns("", size, consumer);
  }

  private static void generatePatterns(String pattern, int size, Function<String, Void> consumer) {
    if (size == 0) {
      consumer.apply(pattern);
    } else {
      if (!endsWithThreeLetters(pattern)) {
        generatePatterns(pattern + "a", size - 1, consumer);

        /*
         * To reduce number of generated patterns by merging equivalent patterns
         * (in terms of NameMatcher). This is done by making sure that each
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

      if (!pattern.endsWith(SINGLE_STAR)) {
        generatePatterns(pattern + SINGLE_STAR, size - 1, consumer);
      }
    }
  }
}
