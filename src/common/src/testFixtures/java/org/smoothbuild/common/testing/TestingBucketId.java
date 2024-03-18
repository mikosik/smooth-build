package org.smoothbuild.common.testing;

import org.smoothbuild.common.bucket.base.BucketId;

public class TestingBucketId {
  public static BucketId bucketId(String id) {
    return new MyBucketId(id);
  }
}

record MyBucketId(String id) implements BucketId {
  @Override
  public String get() {
    return id;
  }
}
