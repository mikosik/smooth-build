package org.smoothbuild.io.fs.base;

import static java.util.Arrays.stream;
import static java.util.regex.Pattern.quote;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.util.Lists.list;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Path {
  public static final String SEPARATOR = "/";
  private static final String ROOT = ".";

  private final String value;

  public static Path path(String value) {
    if (value.equals(ROOT)) {
      return root();
    }
    failIf(value.isEmpty(), "Path cannot be empty string.");
    failIf(value.contains("//"), "Path cannot contain two slashes '//' in a row.");
    failIf(value.startsWith("/"), "Path cannot start with slash character '/'.");
    failIf(value.endsWith("/"), "Path cannot end with slash character '/'.");
    failIf(list(value.split(quote(SEPARATOR))).contains("."),
        "Path cannot contain '.' part unless it is path denoting root dir ('.').");
    failIf(list(value.split(quote(SEPARATOR))).contains(".."),
        "Path cannot contain '..'.");
    return new Path(value);
  }

  private static void failIf(boolean illegal, String message) {
    if (illegal) {
      throw new IllegalPathException(message);
    }
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
    return value.equals(ROOT);
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

  public Path appendPart(String part) {
    if (part.contains(SEPARATOR) ) {
      throw new IllegalArgumentException(
          "Part cannot contain '" + SEPARATOR + "' (part='" + part + "').");
    }
    if (part.equals("") || part.equals(".") || part.equals("..")) {
      throw new IllegalArgumentException(
          "Part cannot be equal to '" + part + "'.");
    }
    if (isRoot()) {
      return new Path(part);
    } else {
      return new Path(this.value + SEPARATOR + part);
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
      return new Path(value.substring(index + 1));
    }
  }

  public String q() {
    return "'" + value +"'";
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
    return value;
  }

  public java.nio.file.Path toJPath() {
    return Paths.get(value);
  }
}
