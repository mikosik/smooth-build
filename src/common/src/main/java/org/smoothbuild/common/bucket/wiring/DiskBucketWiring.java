package org.smoothbuild.common.bucket.wiring;

import com.google.inject.AbstractModule;
import java.nio.file.Path;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.collect.Map;

public class DiskBucketWiring extends AbstractModule {
  private final Map<BucketId, Path> bucketIdToPath;

  public DiskBucketWiring(Map<BucketId, Path> bucketIdToPath) {
    this.bucketIdToPath = bucketIdToPath;
  }

  @Override
  protected void configure() {
    bind(BucketFactory.class).toInstance(new DiskBucketFactory(bucketIdToPath));
  }
}
