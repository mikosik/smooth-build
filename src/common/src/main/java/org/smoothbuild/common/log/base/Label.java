package org.smoothbuild.common.log.base;

import com.google.common.base.CharMatcher;

public record Label(String label) {
  private static final String DELIMITER = ":";
  public static final String ALLOWED_CHARS =
      ":abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final CharMatcher ALLOWED_CHARS_MATCHER = CharMatcher.anyOf(ALLOWED_CHARS);

  public static Label label(String label) {
    return new Label(label);
  }

  public Label {
    verify(label);
  }

  public Label append(String suffix) {
    verify(suffix);
    return new Label(label + suffix);
  }

  public boolean startsWith(Label prefix) {
    return label.startsWith(prefix.label);
  }

  @Override
  public String toString() {
    return label;
  }

  private static void verify(String label) {
    if (label.length() < 2) {
      throw new IllegalArgumentException("Label must have at least 2 characters.");
    }
    if (!ALLOWED_CHARS_MATCHER.matchesAllOf(label)) {
      throw new IllegalArgumentException("Illegal character in label name '" + label + "'.");
    }
    if (!label.startsWith(DELIMITER)) {
      throw new IllegalArgumentException(
          "Label '" + label + "' must start with '" + DELIMITER + "'.");
    }
    if (label.endsWith(DELIMITER)) {
      throw new IllegalArgumentException(
          "Label '" + label + "' cannot end with '" + DELIMITER + "'.");
    }
    if (label.contains(DELIMITER + DELIMITER)) {
      throw new IllegalArgumentException(
          "Label '" + label + "' cannot contain '" + DELIMITER + DELIMITER + "'.");
    }
  }
}
