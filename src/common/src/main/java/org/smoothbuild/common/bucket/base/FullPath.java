package org.smoothbuild.common.bucket.base;

/**
 * This class is immutable.
 */
public record FullPath(Alias alias, Path path) {

  public static FullPath fullPath(Alias alias, String path) {
    return new FullPath(alias, Path.path(path));
  }

  public static FullPath fullPath(Alias alias, Path path) {
    return new FullPath(alias, path);
  }

  public FullPath appendPart(String part) {
    return fullPath(alias, path.appendPart(part));
  }

  public FullPath append(String path) {
    return append(Path.path(path));
  }

  public FullPath append(Path path) {
    return fullPath(alias, this.path.append(path));
  }

  public FullPath parent() {
    return fullPath(alias, path.parent());
  }

  public FullPath withExtension(String extension) {
    return fullPath(alias, path.withExtension(extension));
  }

  public String q() {
    return "'" + this + "'";
  }

  @Override
  public String toString() {
    return "{" + alias().name() + "}/" + path;
  }
}
