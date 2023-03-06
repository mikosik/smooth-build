package org.smoothbuild.slib.file.match;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;

import org.smoothbuild.fs.base.PathS;

public class PathMatcher {
  public static Predicate<PathS> pathMatcher(String pattern) {
    validatePattern(pattern);
    return path -> jdkPathMatcher(pattern).matches(Path.of(path.value()));
  }

  private static java.nio.file.PathMatcher jdkPathMatcher(String patternString) {
    try {
      return FileSystems.getDefault().getPathMatcher("glob:" + patternString);
    } catch (PatternSyntaxException e) {
      throw new IllegalPathPatternExc(e.getMessage(), e);
    }
  }
  private static void validatePattern(String pattern) {
    if (pattern.isEmpty()) {
      throw new IllegalPathPatternExc("Empty pattern is not allowed");
    }
    if (pattern.startsWith("/")) {
      throw new IllegalPathPatternExc("Pattern can't start with slash character '/'.");
    }
    if (pattern.endsWith("/")) {
      throw new IllegalPathPatternExc("Pattern can't end with slash character '/'.");
    }

    if (pattern.contains("//")) {
      throw new IllegalPathPatternExc("Pattern can't contain two slashes (//) in a row");
    }
    if (pattern.equals(".") || pattern.startsWith("./") || pattern.contains("/./")
        || pattern.endsWith("/.")) {
      throw new IllegalPathPatternExc("Pattern can't contain '.' elem.");
    }
    if (pattern.equals("..") || pattern.startsWith("../") || pattern.contains("/../")
        || pattern.endsWith("/..")) {
      throw new IllegalPathPatternExc("Pattern can't contain '..' elem.");
    }
  }
}
