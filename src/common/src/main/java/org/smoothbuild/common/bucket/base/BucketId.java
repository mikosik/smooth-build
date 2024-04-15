package org.smoothbuild.common.bucket.base;

public record BucketId(String id) {
  public static BucketId bucketId(String id) {
    return new BucketId(id);
  }
}
