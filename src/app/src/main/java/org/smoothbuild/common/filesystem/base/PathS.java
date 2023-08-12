package org.smoothbuild.common.filesystem.base;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.List;

public class PathS {
  public static final String SEPARATOR = "/";
  private static final PathS ROOT = new PathS(".");

  private final String value;

  public static PathS path(String value) {
    if (value.equals(ROOT.value)) {
      return ROOT;
    }
    failIfNotLegalPath(value);
    return new PathS(value);
  }

  public static void failIfNotLegalPath(String value) {
    String error = detectPathError(value);
    if (error != null) {
      throw new IllegalPathExc(error);
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

  public static PathS root() {
    return ROOT;
  }

  private PathS(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public boolean isRoot() {
    return this == ROOT;
  }

  public PathS parent() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return parent of root path");
    }
    int index = value.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return root();
    } else {
      return new PathS(value.substring(0, index));
    }
  }

  public boolean startsWith(PathS path) {
    List<PathS> thisParts = parts();
    List<PathS> thatParts = path.parts();
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

  public PathS append(PathS path) {
    if (isRoot()) {
      return path;
    } else if (path.isRoot()) {
      return this;
    } else {
      return new PathS(this.value + SEPARATOR + path.value);
    }
  }

  public PathS appendPart(String part) {
    if (part.contains(SEPARATOR) ) {
      throw new IllegalArgumentException(
          "Part cannot contain '" + SEPARATOR + "' (part='" + part + "').");
    }
    if (part.equals("") || part.equals(".") || part.equals("..")) {
      throw new IllegalArgumentException(
          "Part cannot be equal to '" + part + "'.");
    }
    if (isRoot()) {
      return new PathS(part);
    } else {
      return new PathS(this.value + SEPARATOR + part);
    }
  }

  public PathS changeExtension(String extension) {
    failWithIllegalArgumentIf(isRoot(), "Cannot change extension of '.'.");
    failWithIllegalArgumentIf(
        extension.contains("."), "Extension cannot contain '.', but = " + extension);
    failWithIllegalArgumentIf(
        extension.contains("/"), "Extension cannot contain '/', but = " + extension);
    String withoutExtension = removeExtension(value);
    if (extension.isEmpty()) {
      return new PathS(withoutExtension);
    } else {
      return new PathS(withoutExtension + "." + extension);
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

  public List<PathS> parts() {
    if (isRoot()) {
      return new ArrayList<>();
    } else {
      return stream(value.split(quote(SEPARATOR))).map(PathS::path).toList();
    }
  }

  public PathS firstPart() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return first part of root path.");
    }
    int index = value.indexOf(SEPARATOR);
    if (index == -1) {
      return this;
    } else {
      return new PathS(value.substring(0, index));
    }
  }

  public PathS lastPart() {
    if (isRoot()) {
      throw new IllegalArgumentException("Cannot return last part of root path.");
    }
    int index = value.lastIndexOf(SEPARATOR);
    if (index == -1) {
      return this;
    } else {
      return new PathS(value.substring(index + 1));
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
    return object instanceof PathS that
        && this.value.equals(that.value);
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
