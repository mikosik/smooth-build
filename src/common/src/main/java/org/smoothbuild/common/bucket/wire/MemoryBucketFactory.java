package org.smoothbuild.common.bucket.wire;

import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;

public class MemoryBucketFactory implements BucketFactory {
  @Override
  public Bucket create(Alias alias) {
    return new SynchronizedBucket(new MemoryBucket());
  }
}
