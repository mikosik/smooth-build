package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.virtualmachine.testing.VmTestApi.PROJECT;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullFileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.SynchronizedBucket;
import org.smoothbuild.common.filesystem.mem.MemoryBucket;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VmWiring;

public class VmTestWiring extends AbstractModule {
  private final Map<Alias, FileSystem<Path>> buckets;

  public VmTestWiring() {
    this(map(PROJECT, new SynchronizedBucket(new MemoryBucket())));
  }

  public VmTestWiring(Map<Alias, FileSystem<Path>> buckets) {
    this.buckets = buckets;
  }

  @Override
  protected void configure() {
    install(new VmWiring());
  }

  @Provides
  @Singleton
  public FileSystem<FullPath> provideFilesystem() {
    return new FullFileSystem(buckets);
  }

  @Provides
  @BytecodeDb
  public FullPath provideBytecodeDbPath() {
    return VmTestApi.BYTECODE_DB_PATH;
  }

  @Provides
  @ComputationDb
  public FullPath provideComputationDbPath() {
    return VmTestApi.COMPUTATION_DB_PATH;
  }

  @Provides
  @Project
  public FullPath provideProjectPath() {
    return VmTestApi.PROJECT_PATH;
  }

  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash() {
    return Hash.of(33);
  }
}
