package org.smoothbuild.common.bucket.wire;

import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;

public interface BucketFactory {
  public Bucket create(Alias alias);
}
