package org.smoothbuild.common.filesystem;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.disk.DiskFileSystem;

@Module
public interface FileSystemModule {
  @Provides
  @PerCommand
  static FileSystem<FullPath> provideFilesystem(Map<Alias, Path> aliasToPath) {
    return new SynchronizedFileSystem<>(new DiskFileSystem(aliasToPath));
  }
}
