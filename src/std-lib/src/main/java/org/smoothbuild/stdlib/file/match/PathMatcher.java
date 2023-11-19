package org.smoothbuild.stdlib.file.match;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;

import org.smoothbuild.common.filesystem.base.PathS;

public class PathMatcher implements Predicate<PathS> {
  private final FileSystem fileSystem;
  private final java.nio.file.PathMatcher pathMatcher;

  public PathMatcher(String pattern) {
    validatePattern(pattern);
    this.fileSystem = FileSystems.getDefault();
    try {
      this.pathMatcher = fileSystem.getPathMatcher("glob:" + pattern);
    } catch (PatternSyntaxException e) {
      throw new IllegalPathPatternException(e.getMessage(), e);
    }
  }

  @Override
  public boolean test(PathS pathS) {
    return pathMatcher.matches(fileSystem.getPath(pathS.value()));
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
      throw new IllegalPathPatternException("Pattern can't contain '.' elem.");
    }
    if (pattern.equals("..") || pattern.startsWith("../") || pattern.contains("/../")
        || pattern.endsWith("/..")) {
      throw new IllegalPathPatternException("Pattern can't contain '..' elem.");
    }
  }
}
