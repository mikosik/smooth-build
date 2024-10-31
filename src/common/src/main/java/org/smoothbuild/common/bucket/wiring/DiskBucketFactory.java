package org.smoothbuild.common.bucket.wiring;

import java.nio.file.Path;
import java.util.Map;
import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.disk.DiskBucket;

public class DiskBucketFactory implements BucketFactory {
  private final Map<Alias, Path> aliasToPath;

  public DiskBucketFactory(Map<Alias, Path> aliasToPath) {
    this.aliasToPath = aliasToPath;
  }

  @Override
  public Bucket create(Alias alias) {
    return new SynchronizedBucket(new DiskBucket(aliasToPath.get(alias)));
  }
}
