package org.smoothbuild.io.fs.base;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Splitter;

/**
 * Path within a project.
 */
public class Path {
  public static final char SEPARATOR_CHARACTER = '/';
  public static final String SEPARATOR = new String(new char[] { SEPARATOR_CHARACTER });
  private static final String ROOT = SEPARATOR;

  private final String value;

  public static Path path(String value) {
    checkIsValid(value);
    if (isRootString(value)) {
      return root();
    } else {
      return new Path(value);
    }
  }

  public static Path root() {
    return new Path(ROOT);
  }

  private Path(String value) {
    this.value = value;
  }

  private static void checkIsValid(String value) {
    String message = validationError(value);
    if (message != null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static String validationError(String path) {
    if (path.isEmpty()) {
      return "Empty path is not allowed";
    }
    if (!isRootString(path)) {
      if (path.startsWith("/")) {
        return "Path cannot start with slash character '/'.";
      }
      if (path.endsWith("/")) {
        return "Path cannot end with slash character '/'.";
      }
    }
    if (path.contains("//")) {
      return "Path cannot contain two slashes (//) in a row";
    }
    if (path.equals(".") || path.startsWith("./") || path.contains("/./") || path.endsWith("/.")) {
      return "Path cannot contain '.' element.";
    }
    if (path.equals("..") || path.startsWith("../") || path.contains("/../")
        || path.endsWith("/..")) {
      return "Path cannot contain '..' element.";
    }
    return null;
  }

  public String value() {
    return value;
  }

  public boolean isRoot() {
    return isRootString(value);
  }

  private static boolean isRootString(String path) {
    return path.equals(ROOT);
  }

  public Path parent() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return parent of root path '.'");
    }
    int index = value.lastIndexOf(SEPARATOR_CHARACTER);
    if (index == -1) {
      return root();
    } else {
      return new Path(value.substring(0, index));
    }
  }

  public Path append(Path path) {
    if (isRoot()) {
      return path;
    } else if (path.isRoot()) {
      return this;
    } else {
      return new Path(this.value + SEPARATOR + path.value);
    }
  }

  public List<Path> parts() {
    if (isRoot()) {
      return new ArrayList<>();
    } else {
      List<Path> result = new ArrayList<>();
      for (String string : Splitter.on(SEPARATOR_CHARACTER).split(value)) {
        result.add(new Path(string));
      }
      return result;
    }
  }

  public Path firstPart() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return first part of root path '/'");
    }
    int index = value.indexOf(SEPARATOR_CHARACTER);
    if (index == -1) {
      return this;
    } else {
      return new Path(value.substring(0, index));
    }
  }

  public Path lastPart() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return last part of root path '/'");
    }
    int index = value.lastIndexOf(SEPARATOR_CHARACTER);
    if (index == -1) {
      return this;
    } else {
      return new Path(value.substring(index + 1, value.length()));
    }
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object instanceof Path) {
      Path that = (Path) object;
      return this.value.equals(that.value);
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "'" + value + "'";
  }
}
