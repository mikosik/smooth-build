package org.smoothbuild.common.bucket.wiring;

import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

public class MemoryBucketFactory implements BucketFactory {
  @Override
  public Bucket create(BucketId bucketId) {
    return new SynchronizedBucket(new MemoryBucket());
  }
}
