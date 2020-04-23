package org.smoothbuild.builtin.file.match;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.io.fs.base.Path;

public class PathPattern {
  private static final String DOUBLE_STAR_ERROR_MESSAGE = "Pattern can contain '**' only when "
      + "it is surrounded by '/' or placed at the beginning or the end of pattern.";

  private final String value;

  public static PathPattern pathPattern(String value) {
    checkIsValid(value);
    return new PathPattern(value);
  }

  private PathPattern(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public Iterable<String> parts() {
    return list(value.split(Path.SEPARATOR));
  }

  private static void checkIsValid(String value) {
    String message = validationError(value);
    if (message != null) {
      throw new IllegalPathPatternException(message);
    }
  }

  private static String validationError(String pattern) {
    if (pattern.isEmpty()) {
      return "Empty pattern is not allowed";
    }
    if (pattern.startsWith("/")) {
      return "Pattern can't start with slash character '/'.";
    }
    if (pattern.endsWith("/")) {
      return "Pattern can't end with slash character '/'.";
    }

    if (pattern.contains("//")) {
      return "Pattern can't contain two slashes (//) in a row";
    }
    if (pattern.equals(".") || pattern.startsWith("./") || pattern.contains("/./")
        || pattern.endsWith("/.")) {
      return "Pattern can't contain '.' element.";
    }
    if (pattern.equals("..") || pattern.startsWith("../") || pattern.contains("/../")
        || pattern.endsWith("/..")) {
      return "Pattern can't contain '..' element.";
    }

    if (pattern.contains("***")) {
      return "Pattern cannot contain more than two '*' in a row.";
    }

    int index = 0;
    index = pattern.indexOf(Constants.DOUBLE_STAR, index);
    while (index != -1) {
      if (index != 0 && pattern.charAt(index - 1) != '/') {
        return DOUBLE_STAR_ERROR_MESSAGE;
      }
      int length = Constants.DOUBLE_STAR.length();
      if (index < pattern.length() - length && pattern.charAt(index + length) != '/') {
        return DOUBLE_STAR_ERROR_MESSAGE;
      }
      index = pattern.indexOf(Constants.DOUBLE_STAR, index + 1);
    }

    if (pattern.contains("**/**")) {
      return "Pattern can't contain '**/**' element. Replace it with single '**'.";
    }
    return null;
  }
}
