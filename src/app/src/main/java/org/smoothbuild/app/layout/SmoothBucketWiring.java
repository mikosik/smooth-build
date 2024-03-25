package org.smoothbuild.app.layout;

import static org.smoothbuild.app.layout.SmoothBucketId.BINARY;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;
import static org.smoothbuild.app.layout.SmoothBucketId.STANDARD_LIBRARY;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.util.HashMap;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.wiring.BucketFactory;
import org.smoothbuild.common.collect.Map;

public class SmoothBucketWiring extends AbstractModule {
  private final boolean withProjectBucket;

  public SmoothBucketWiring(boolean withProjectBucket) {
    this.withProjectBucket = withProjectBucket;
  }

  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public Map<BucketId, Bucket> provideBucketIdToBucketMap(BucketFactory bucketFactory) {
    java.util.Map<BucketId, Bucket> map = new HashMap<>();
    if (withProjectBucket) {
      map.put(PROJECT, bucketFactory.create(PROJECT));
    }
    map.put(STANDARD_LIBRARY, bucketFactory.create(STANDARD_LIBRARY));
    map.put(BINARY, bucketFactory.create(BINARY));
    return Map.mapOfAll(map);
  }
}