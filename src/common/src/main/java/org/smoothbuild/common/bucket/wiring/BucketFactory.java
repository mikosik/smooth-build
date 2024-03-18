package org.smoothbuild.common.bucket.wiring;

import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;

public interface BucketFactory {
  public Bucket create(BucketId bucketId);
}
