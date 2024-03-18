package org.smoothbuild.common.bucket.wiring;

import com.google.inject.AbstractModule;

public class MemoryBucketModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(BucketFactory.class).to(MemoryBucketFactory.class);
  }
}
