package org.smoothbuild.app.layout;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.wiring.BucketFactory;

public class ProjectBucketModule extends AbstractModule {
  @Override
  protected void configure() {
    BucketIdUtils.addMapBindingForBucket(binder(), SmoothBucketId.PROJECT);
  }

  @Provides
  @Singleton
  @ForBucket(SmoothBucketId.PROJECT)
  public Bucket provideBucket(BucketFactory bucketFactory) {
    return bucketFactory.create(SmoothBucketId.PROJECT);
  }
}
