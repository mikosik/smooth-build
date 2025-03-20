package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.Set.set;
import static org.smoothbuild.virtualmachine.testing.VmTestApi.BYTECODE_DB_PATH;
import static org.smoothbuild.virtualmachine.testing.VmTestApi.COMPUTATION_DB_PATH;
import static org.smoothbuild.virtualmachine.testing.VmTestApi.PROJECT;
import static org.smoothbuild.virtualmachine.testing.VmTestApi.PROJECT_PATH;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.SynchronizedFileSystem;
import org.smoothbuild.common.filesystem.mem.MemoryFileSystem;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VmWiring;

public class VmTestWiring extends AbstractModule {
  private final FileSystem<FullPath> fileSystem;

  public VmTestWiring() {
    this(new SynchronizedFileSystem<>(new MemoryFileSystem(set(PROJECT))));
  }

  public VmTestWiring(FileSystem<FullPath> fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  protected void configure() {
    install(new VmWiring());
  }

  @Provides
  @Singleton
  public FileSystem<FullPath> provideFilesystem() {
    return fileSystem;
  }

  @Provides
  @BytecodeDb
  public FullPath provideBytecodeDbPath() {
    return BYTECODE_DB_PATH;
  }

  @Provides
  @ComputationDb
  public FullPath provideComputationDbPath() {
    return COMPUTATION_DB_PATH;
  }

  @Provides
  @Project
  public FullPath provideProjectPath() {
    return PROJECT_PATH;
  }

  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash() {
    return Hash.of(33);
  }
}
