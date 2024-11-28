package org.smoothbuild.common.log.base;

import com.google.common.base.CharMatcher;

public record Label(String label) {
  private static final String DELIMITER = ":";
  public static final CharMatcher ALLOWED_CHARS_MATCHER =
      CharMatcher.anyOf(":abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

  public static Label label(String label) {
    return new Label(label);
  }

  public Label {
    if (label.isEmpty()) {
      throw new IllegalArgumentException("Label cannot be empty string.");
    }
    if (!ALLOWED_CHARS_MATCHER.matchesAllOf(label)) {
      throw new IllegalArgumentException("Illegal character in lable name '" + label + "'.");
    }
    if (label.startsWith(DELIMITER)) {
      throw new IllegalArgumentException(
          "Label '" + label + "' cannot start with '" + DELIMITER + "'.");
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

  public Label append(String suffix) {
    return new Label(label + DELIMITER + suffix);
  }

  public boolean startsWith(Label prefix) {
    return label.startsWith(prefix.label);
  }

  @Override
  public String toString() {
    return DELIMITER + label;
  }
}
