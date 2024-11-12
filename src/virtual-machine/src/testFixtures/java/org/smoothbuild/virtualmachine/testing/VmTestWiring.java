package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.virtualmachine.testing.VmTestApi.PROJECT;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VmWiring;

public class VmTestWiring extends AbstractModule {
  private final Map<Alias, Bucket> buckets;

  public VmTestWiring() {
    this(map(PROJECT, new SynchronizedBucket(new MemoryBucket())));
  }

  public VmTestWiring(Map<Alias, Bucket> buckets) {
    this.buckets = buckets;
  }

  @Override
  protected void configure() {
    install(new VmWiring());
  }

  @Provides
  @Singleton
  public Map<Alias, Bucket> provideBucketMap() {
    return buckets;
  }

  @Provides
  @BytecodeDb
  public FullPath provideBytecodeDb() {
    return VmTestApi.BYTECODE_DB_PATH;
  }

  @Provides
  @ComputationDb
  public FullPath provideComputationDb() {
    return VmTestApi.COMPUTATION_DB_PATH;
  }

  @Provides
  @Project
  public FullPath provideProject() {
    return VmTestApi.PROJECT_PATH;
  }

  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash() {
    return Hash.of(33);
  }
}
