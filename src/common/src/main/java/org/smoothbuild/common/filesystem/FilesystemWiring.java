package org.smoothbuild.common.filesystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullFileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.disk.DiskBucket;

public class FilesystemWiring extends AbstractModule {
  private final Map<Alias, Path> aliasToPath;

  public FilesystemWiring(Map<Alias, Path> aliasToPath) {
    this.aliasToPath = aliasToPath;
  }

  @Provides
  @Singleton
  public FileSystem<FullPath> provideFilesystem() {
    return new SynchronizedFileSystem<>(new FullFileSystem(aliasToPath.mapValues(DiskBucket::new)));
  }
}
