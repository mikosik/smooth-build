package org.smoothbuild.common.testing;

import org.smoothbuild.common.bucket.base.BucketId;

public class TestingBucketId {
  public static final BucketId BUCKET_ID = new BucketId("bucket-id");
  public static final BucketId UNKNOWN_ID = new BucketId("unknown");
  public static final BucketId PROJECT = new BucketId("t-project");
  public static final BucketId LIBRARY = new BucketId("t-library");
  public static final BucketId INSTALL = new BucketId("t-install");
}
