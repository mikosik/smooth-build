package org.smoothbuild.common.bucket.wiring;

import jakarta.inject.Inject;
import java.nio.file.Path;
import java.util.Map;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.disk.DiskBucket;

public class DiskBucketFactory implements BucketFactory {
  private final Map<BucketId, Path> bucketIdToPath;

  @Inject
  public DiskBucketFactory(Map<BucketId, Path> bucketIdToPath) {
    this.bucketIdToPath = bucketIdToPath;
  }

  @Override
  public Bucket create(BucketId bucketId) {
    return new SynchronizedBucket(new DiskBucket(bucketIdToPath.get(bucketId)));
  }
}
