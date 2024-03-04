package org.smoothbuild;

import static org.smoothbuild.layout.Layout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.layout.SmoothSpace.PROJECT;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.SubFileSystem;
import org.smoothbuild.layout.ForSpace;
import org.smoothbuild.virtualmachine.evaluate.BytecodeDb;
import org.smoothbuild.virtualmachine.evaluate.ComputationDb;
import org.smoothbuild.virtualmachine.evaluate.ProjectFs;

public class VirtualMachineConfigurationModule extends AbstractModule {
  @Provides
  @ComputationDb
  public FileSystem provideComputationCacheFileSystem(@ForSpace(PROJECT) FileSystem fileSystem) {
    return new SubFileSystem(fileSystem, COMPUTATION_CACHE_PATH);
  }

  @Provides
  @BytecodeDb
  public FileSystem provideBytecodeDbFileSystem(@ForSpace(PROJECT) FileSystem fileSystem) {
    return new SubFileSystem(fileSystem, HASHED_DB_PATH);
  }

  @Provides
  @ProjectFs
  public FileSystem provideProjectFileSystem(@ForSpace(PROJECT) FileSystem fileSystem) {
    return fileSystem;
  }
}
