package org.smoothbuild.common.bucket;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.disk.DiskBucket;
import org.smoothbuild.common.collect.Map;

public class FilesystemWiring extends AbstractModule {
  private final Map<Alias, Path> aliasToPath;

  public FilesystemWiring(Map<Alias, Path> aliasToPath) {
    this.aliasToPath = aliasToPath;
  }

  @Provides
  @Singleton
  public Filesystem provideFilesystem() {
    return new Filesystem(aliasToPath.mapValues(p -> new SynchronizedBucket(new DiskBucket(p))));
  }
}
