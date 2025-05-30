package org.smoothbuild.common.dagger;

import dagger.Module;
import dagger.Provides;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;

@Module
public interface FileSystemTestModule {
  @Provides
  @PerCommand
  static FileSystem<FullPath> provideFilesystem(Set<Alias> aliases) {
    return new SynchronizedFileSystem<>(new MemoryFileSystem(aliases));
  }
}
