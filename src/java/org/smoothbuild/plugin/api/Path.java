package org.smoothbuild.plugin.api;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Path within a project.
 */
public class Path {
  public static final char SEPARATOR_CHARACTER = '/';
  public static final String SEPARATOR = new String(new char[] { SEPARATOR_CHARACTER });
  private static final String ROOT_STRING = ".";
  private static final Path ROOT_PATH = new Path(ROOT_STRING);

  private final String value;

  public static Path path(String value) {
    checkIsValid(value);
    String canonicalValue = toCanonical(value);
    if (canonicalValue.equals(ROOT_STRING)) {
      return ROOT_PATH;
    } else {
      return new Path(canonicalValue);
    }
  }

  public static Path rootPath() {
    return ROOT_PATH;
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
      return "Empty paths are not allowed";
    }
    if (path.startsWith("/")) {
      return "Path cannot start with slash character '/'. Only paths relative to project root dir are allowed";
    }
    if (path.endsWith("/")) {
      return "Path cannot end with slash character '/'.";
    }
    if (path.contains("//")) {
      return "Path cannot contain two slashes (//) in a row";
    }
    if (path.contains("/./") || path.endsWith("/.")) {
      return "Path can contain '.' element only at the beginning (for example './mypath').";
    }
    if (path.equals("..") || path.startsWith("../") || path.contains("/../")
        || path.endsWith("/..")) {
      return "Path cannot contain '..' element. Referencing files outside your project is a bad idea.";
    }
    return null;
  }

  private static String toCanonical(String path) {
    // remove './' prefix
    return path.startsWith("./") ? path.substring(2, path.length()) : path;
  }

  public String value() {
    return value;
  }

  public boolean isRoot() {
    return this == ROOT_PATH;
  }

  public Path parent() {
    if (this == ROOT_PATH) {
      throw new IllegalArgumentException("Cannot return parent of root path '.'");
    }
    int index = value.lastIndexOf(SEPARATOR_CHARACTER);
    if (index == -1) {
      return ROOT_PATH;
    } else {
      return new Path(value.substring(0, index));
    }
  }

  public Path append(Path path) {
    if (this == ROOT_PATH) {
      return path;
    } else if (path == ROOT_PATH) {
      return this;
    } else {
      return new Path(this.value + SEPARATOR + path.value);
    }
  }

  public ImmutableList<Path> toElements() {
    if (this == ROOT_PATH) {
      return ImmutableList.<Path> of();
    } else {
      Builder<Path> builder = ImmutableList.builder();
      for (String string : Splitter.on(SEPARATOR_CHARACTER).split(value)) {
        builder.add(new Path(string));
      }
      return builder.build();
    }
  }

  public Path firstElement() {
    if (this == ROOT_PATH) {
      throw new IllegalArgumentException("Cannot return first element of root path '.'");
    }
    int index = value.indexOf(SEPARATOR_CHARACTER);
    if (index == -1) {
      return this;
    } else {
      return new Path(value.substring(0, index));
    }
  }

  public Path lastElement() {
    if (this == ROOT_PATH) {
      throw new IllegalArgumentException("Cannot return last element of root path '.'");
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
