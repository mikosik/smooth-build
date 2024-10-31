package org.smoothbuild.common.bucket.base;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Map;

public class BucketResolver {
  private final Map<Alias, Bucket> buckets;

  @Inject
  public BucketResolver(Map<Alias, Bucket> buckets) {
    this.buckets = buckets;
  }

  public Bucket bucketFor(Alias alias) {
    Bucket bucket = buckets.get(alias);
    if (bucket == null) {
      throw new IllegalArgumentException(
          "Unknown alias " + alias + ". Known aliases = " + buckets.keySet());
    }
    return bucket;
  }
}
