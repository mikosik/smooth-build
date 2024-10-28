package org.smoothbuild.common.bucket.base;

import static org.smoothbuild.common.bucket.base.FullPath.fullPath;

public record BucketId(String id) {
  public static BucketId bucketId(String id) {
    return new BucketId(id);
  }

  public FullPath path(String path) {
    return path(Path.path(path));
  }

  public FullPath path(Path path) {
    return fullPath(this, path);
  }
}
