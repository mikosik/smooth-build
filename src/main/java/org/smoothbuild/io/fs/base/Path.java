package org.smoothbuild.io.fs.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toList;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Path {
  public static final String SEPARATOR = "/";
  private static final String ROOT = "";

  private final String value;

  public static Path path(String value) {
    checkArgument(!value.startsWith("/"), "Path cannot start with slash character '/'.");
    checkArgument(!value.endsWith("/"), "Path cannot end with slash character '/'.");
    checkArgument(!value.contains("//"), "Path cannot contain two slashes (//) in a row");
    checkArgument(!asList(value.split(quote(SEPARATOR))).contains("."),
        "Path cannot contain '.' element.");
    checkArgument(!asList(value.split(quote(SEPARATOR))).contains(".."),
        "Path cannot contain '..' element.");
    return new Path(value);
  }

  public static Path root() {
    return new Path(ROOT);
  }

  private Path(String value) {
    this.value = value;
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
      throw new IllegalArgumentException("Cannot return parent of root path");
    }
    int index = value.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return root();
    } else {
      return new Path(value.substring(0, index));
    }
  }

  public boolean startsWith(Path path) {
    List<Path> thisParts = parts();
    List<Path> thatParts = path.parts();
    if (thisParts.size() < thatParts.size()) {
      return false;
    }
    for (int i = 0; i < thatParts.size(); i++) {
      if (!thisParts.get(i).equals(thatParts.get(i))) {
        return false;
      }
    }
    return true;
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
      return stream(value.split(quote(SEPARATOR))).map(Path::path).collect(toList());
    }
  }

  public Path firstPart() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return first part of root path.");
    }
    int index = value.indexOf(SEPARATOR);
    if (index == -1) {
      return this;
    } else {
      return new Path(value.substring(0, index));
    }
  }

  public Path lastPart() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return last part of root path.");
    }
    int index = value.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return this;
    } else {
      return new Path(value.substring(index + 1, value.length()));
    }
  }

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

  public final int hashCode() {
    return value.hashCode();
  }

  public String toString() {
    return "'" + value + "'";
  }

  public java.nio.file.Path toJPath() {
    if (isRoot()) {
      return Paths.get(".");
    } else {
      return Paths.get(value);
    }
  }
}
