package org.smoothbuild.slib.file.match;

import java.nio.file.FileSystems;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;

import org.smoothbuild.io.fs.base.Path;

public class PathMatcher {
  public static Predicate<Path> pathMatcher(String pattern) {
    validatePattern(pattern);
    return path -> jdkPathMatcher(pattern).matches(java.nio.file.Path.of(path.value()));
  }

  private static java.nio.file.PathMatcher jdkPathMatcher(String patternString) {
    try {
      return FileSystems.getDefault().getPathMatcher("glob:" + patternString);
    } catch (PatternSyntaxException e) {
      throw new IllegalPathPatternException(e.getMessage(), e);
    }
  }
  private static void validatePattern(String pattern) {
    if (pattern.isEmpty()) {
      throw new IllegalPathPatternException("Empty pattern is not allowed");
    }
    if (pattern.startsWith("/")) {
      throw new IllegalPathPatternException("Pattern can't start with slash character '/'.");
    }
    if (pattern.endsWith("/")) {
      throw new IllegalPathPatternException("Pattern can't end with slash character '/'.");
    }

    if (pattern.contains("//")) {
      throw new IllegalPathPatternException("Pattern can't contain two slashes (//) in a row");
    }
    if (pattern.equals(".") || pattern.startsWith("./") || pattern.contains("/./")
        || pattern.endsWith("/.")) {
      throw new IllegalPathPatternException("Pattern can't contain '.' element.");
    }
    if (pattern.equals("..") || pattern.startsWith("../") || pattern.contains("/../")
        || pattern.endsWith("/..")) {
      throw new IllegalPathPatternException("Pattern can't contain '..' element.");
    }
  }
}
