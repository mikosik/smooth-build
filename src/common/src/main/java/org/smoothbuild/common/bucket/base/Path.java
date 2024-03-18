package org.smoothbuild.common.bucket.base;

import static java.util.Arrays.asList;
import static java.util.regex.Pattern.quote;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;

public class Path {
  public static final String SEPARATOR = "/";
  private static final Path ROOT = new Path(".");

  private final String value;

  public static Path path(String value) {
    if (value.equals(ROOT.value)) {
      return ROOT;
    }
    failIfNotLegalPath(value);
    return new Path(value);
  }

  public static void failIfNotLegalPath(String value) {
    String error = detectPathError(value);
    if (error != null) {
      throw new IllegalPathException(error);
    }
  }

  public static String detectPathError(String value) {
    if (value.isEmpty()) {
      return "Path cannot be empty string.";
    }
    if (value.contains("//")) {
      return "Path cannot contain two slashes '//' in a row.";
    }
    if (value.startsWith("/")) {
      return "Path cannot start with slash character '/'.";
    }
    if (value.endsWith("/")) {
      return "Path cannot end with slash character '/'.";
    }
    var parts = asList(value.split(quote(SEPARATOR)));
    if (parts.contains(".")) {
      return "Path cannot contain '.' part unless it is path denoting root dir ('.').";
    }
    if (parts.contains("..")) {
      return "Path cannot contain '..' part.";
    }
    return null;
  }

  public static Path root() {
    return ROOT;
  }

  private Path(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public boolean isRoot() {
    return this == ROOT;
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
    if (part.contains(SEPARATOR)) {
      throw new IllegalArgumentException(
          "Part cannot contain '" + SEPARATOR + "' (part='" + part + "').");
    }
    if (part.equals("") || part.equals(".") || part.equals("..")) {
      throw new IllegalArgumentException("Part cannot be equal to '" + part + "'.");
    }
    if (isRoot()) {
      return new Path(part);
    } else {
      return new Path(this.value + SEPARATOR + part);
    }
  }

  public Path changeExtension(String extension) {
    failWithIllegalArgumentIf(isRoot(), "Cannot change extension of '.'.");
    failWithIllegalArgumentIf(
        extension.contains("."), "Extension cannot contain '.', but = " + extension);
    failWithIllegalArgumentIf(
        extension.contains("/"), "Extension cannot contain '/', but = " + extension);
    String withoutExtension = removeExtension(value);
    if (extension.isEmpty()) {
      return new Path(withoutExtension);
    } else {
      return new Path(withoutExtension + "." + extension);
    }
  }

  private void failWithIllegalArgumentIf(boolean illegal, String message) {
    if (illegal) {
      throw new IllegalArgumentException(message);
    }
  }

  private static String removeExtension(String string) {
    int dotIndex = string.lastIndexOf('.');
    if (dotIndex == -1) {
      return string;
    } else {
      return string.substring(0, dotIndex);
    }
  }

  public List<Path> parts() {
    if (isRoot()) {
      return list();
    } else {
      return list(value.split(quote(SEPARATOR))).map(Path::path);
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
    return "'" + value + "'";
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Path that && this.value.equals(that.value);
  }

  @Override
  public final int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}
