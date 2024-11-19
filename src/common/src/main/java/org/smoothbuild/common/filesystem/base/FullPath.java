package org.smoothbuild.common.filesystem.base;

/**
 * This class is immutable.
 */
public record FullPath(Alias alias, Path path) implements PathI<FullPath> {

  public static FullPath fullPath(Alias alias, String path) {
    return new FullPath(alias, Path.path(path));
  }

  public static FullPath fullPath(Alias alias, Path path) {
    return new FullPath(alias, path);
  }

  @Override
  public boolean isRoot() {
    return path.isRoot();
  }

  @Override
  public boolean startsWith(FullPath path) {
    return this.alias.equals(path.alias) && this.path.startsWith(path.path);
  }

  @Override
  public FullPath appendPart(String part) {
    return fullPath(alias, path.appendPart(part));
  }

  public FullPath append(String path) {
    return append(Path.path(path));
  }

  @Override
  public FullPath append(Path path) {
    return fullPath(alias, this.path.append(path));
  }

  @Override
  public FullPath parent() {
    return fullPath(alias, path.parent());
  }

  @Override
  public FullPath withExtension(String extension) {
    return fullPath(alias, path.withExtension(extension));
  }

  @Override
  public String q() {
    return "'" + this + "'";
  }

  @Override
  public String toString() {
    return "{" + alias().name() + "}/" + path;
  }
}
