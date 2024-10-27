package org.smoothbuild.common.bucket.base;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Map;

public class BucketResolver {
  private final Map<BucketId, Bucket> buckets;

  @Inject
  public BucketResolver(Map<BucketId, Bucket> buckets) {
    this.buckets = buckets;
  }

  public Bucket bucketFor(BucketId bucketId) {
    Bucket bucket = buckets.get(bucketId);
    if (bucket == null) {
      throw new IllegalArgumentException("Unknown bucket id " + bucketId + ".");
    }
    return bucket;
  }
}
