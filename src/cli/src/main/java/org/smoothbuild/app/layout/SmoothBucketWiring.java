package org.smoothbuild.app.layout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.wiring.BucketFactory;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Set;

public class SmoothBucketWiring extends AbstractModule {
  private final Set<BucketId> bucketIds;

  public SmoothBucketWiring(Set<BucketId> bucketIds) {
    this.bucketIds = bucketIds;
  }

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public Map<BucketId, Bucket> provideBucketIdToBucketMap(BucketFactory bucketFactory) {
    return bucketIds.toMap(bucketFactory::create);
  }
}
