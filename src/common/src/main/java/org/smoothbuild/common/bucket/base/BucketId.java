package org.smoothbuild.common.bucket.base;

import static org.smoothbuild.common.bucket.base.FullPath.fullPath;

public record BucketId(String id) {
  public static BucketId bucketId(String id) {
    return new BucketId(id);
  }

  public FullPath append(String path) {
    return append(Path.path(path));
  }

  public FullPath append(Path path) {
    return fullPath(this, path);
  }
}
