package org.smoothbuild.common.bucket.wire;

import com.google.inject.AbstractModule;
import java.nio.file.Path;
import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.collect.Map;

public class DiskBucketWiring extends AbstractModule {
  private final Map<Alias, Path> aliasToPath;

  public DiskBucketWiring(Map<Alias, Path> aliasToPath) {
    this.aliasToPath = aliasToPath;
  }

  @Override
  protected void configure() {
    bind(BucketFactory.class).toInstance(new DiskBucketFactory(aliasToPath));
  }
}
