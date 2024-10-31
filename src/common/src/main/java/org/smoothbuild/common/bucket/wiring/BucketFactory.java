package org.smoothbuild.common.bucket.wiring;

import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;

public interface BucketFactory {
  public Bucket create(Alias alias);
}
