package org.smoothbuild.common.bucket.wire;

import com.google.inject.AbstractModule;

public class MemoryBucketWiring extends AbstractModule {
  @Override
  protected void configure() {
    bind(BucketFactory.class).to(MemoryBucketFactory.class);
  }
}
