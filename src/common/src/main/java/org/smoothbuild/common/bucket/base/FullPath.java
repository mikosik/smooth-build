package org.smoothbuild.common.bucket.base;

/**
 * This class is immutable.
 */
public record FullPath(BucketId bucketId, Path path) {

  public static FullPath fullPath(BucketId bucketId, Path path) {
    return new FullPath(bucketId, path);
  }

  public FullPath appendPart(String part) {
    return fullPath(bucketId, path.appendPart(part));
  }

  public FullPath append(Path path) {
    return fullPath(bucketId, this.path.append(path));
  }

  public FullPath parent() {
    return fullPath(bucketId, path.parent());
  }

  public FullPath withExtension(String extension) {
    return fullPath(bucketId, path.changeExtension(extension));
  }

  public String q() {
    return "'" + this + "'";
  }

  @Override
  public String toString() {
    return "{" + bucketId().id() + "}/" + path;
  }
}
