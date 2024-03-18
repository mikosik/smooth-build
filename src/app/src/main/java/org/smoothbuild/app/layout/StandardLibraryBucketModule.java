package org.smoothbuild.app.layout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.wiring.BucketFactory;

public class StandardLibraryBucketModule extends AbstractModule {
  @Override
  protected void configure() {
    BucketIdUtils.addMapBindingForBucket(binder(), SmoothBucketId.STANDARD_LIBRARY);
  }

  @Provides
  @Singleton
  @ForBucket(SmoothBucketId.STANDARD_LIBRARY)
  public Bucket provideBucket(BucketFactory bucketFactory) {
    return bucketFactory.create(SmoothBucketId.STANDARD_LIBRARY);
  }
}
