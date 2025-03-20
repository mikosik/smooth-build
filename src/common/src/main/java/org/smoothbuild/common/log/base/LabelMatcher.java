package org.smoothbuild.common.log.base;

import com.google.common.base.CharMatcher;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class LabelMatcher implements Predicate<Label> {
  public static final CharMatcher ALLOWED_CHARS_MATCHER =
      CharMatcher.anyOf(Label.ALLOWED_CHARS + "*");
  private final Predicate<String> predicate;

  public LabelMatcher(String pattern) {
    this.predicate = parsePatternToPredicate(pattern);
  }

  private static Predicate<String> parsePatternToPredicate(String pattern) {
    var regexBuilder = new StringBuilder("^");
    int asteriskCount = 0;
    for (var character : pattern.toCharArray()) {
      if (!ALLOWED_CHARS_MATCHER.matches(character)) {
        throw new IllegalArgumentException(
            "Pattern contains illegal character '" + character + "'.");
      }
      if (character == '*') {
        asteriskCount++;
      } else {
        appendConvertedAsterisks(asteriskCount, regexBuilder);
        asteriskCount = 0;
        regexBuilder.append(character);
      }
    }
    appendConvertedAsterisks(asteriskCount, regexBuilder);
    regexBuilder.append('$');
    var regex = regexBuilder.toString();
    return Pattern.compile(regex).asPredicate();
  }

  private static void appendConvertedAsterisks(int asteriskCount, StringBuilder builder) {
    switch (asteriskCount) {
      case 0 -> {}
      case 1 -> builder.append("[^:]*");
      case 2 -> builder.append(".*");
      default ->
        throw new IllegalArgumentException(
            "Pattern contains more than 2 consecutive '*' characters.");
    }
  }

  @Override
  public boolean test(Label label) {
    return predicate.test(label.toString());
  }
}
