package org.smoothbuild.virtualmachine.dagger;

import static org.smoothbuild.common.filesystem.base.FileSystemPart.fileSystemPart;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.virtualmachine.bytecode.hashed.HashedDbInitializer;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationCacheInitializer;

@Module
public interface VmModule {
  @Binds
  @IntoSet
  Initializable hashedDbInitializer(HashedDbInitializer i);

  @Binds
  @IntoSet
  Initializable computationCacheInitializer(ComputationCacheInitializer i);

  @Provides
  @BytecodeDb
  static FileSystem<Path> provideBytecodeDbFileSystem(
      FileSystem<FullPath> fileSystem, @BytecodeDb FullPath path) {
    return fileSystemPart(fileSystem, path);
  }

  @Provides
  @ComputationDb
  static FileSystem<Path> provideComputationDbFileSystem(
      FileSystem<FullPath> fileSystem, @ComputationDb FullPath path) {
    return fileSystemPart(fileSystem, path);
  }

  @Provides
  @Project
  static FileSystem<Path> provideProjectFileSystem(
      FileSystem<FullPath> fileSystem, @Project FullPath path) {
    return fileSystemPart(fileSystem, path);
  }
}
