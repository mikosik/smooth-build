package org.smoothbuild.app.layout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.wiring.BucketFactory;

public class BinaryBucketWiring extends AbstractModule {
  @Override
  protected void configure() {
    BucketIdUtils.addMapBindingForBucket(binder(), SmoothBucketId.BINARY);
  }

  @Provides
  @Singleton
  @ForBucket(SmoothBucketId.BINARY)
  public Bucket provideBucket(BucketFactory bucketFactory) {
    return bucketFactory.create(SmoothBucketId.BINARY);
  }
}
