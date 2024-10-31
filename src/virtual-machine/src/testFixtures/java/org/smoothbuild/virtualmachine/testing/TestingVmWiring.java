package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.testing.TestingAlias.PROJECT;
import static org.smoothbuild.common.testing.TestingFullPath.BYTECODE_DB_PATH;
import static org.smoothbuild.common.testing.TestingFullPath.COMPUTATION_DB_PATH;
import static org.smoothbuild.common.testing.TestingFullPath.PROJECT_PATH;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.log.report.ReportWiring;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VmWiring;

public class TestingVmWiring extends AbstractModule {
  @Override
  protected void configure() {
    install(new VmWiring());
    install(new ReportWiring(new PrintWriter(System.out), (label, logs) -> true, INFO));
  }

  @Provides
  @Singleton
  public Map<Alias, Bucket> provideBucketMap() {
    return map(PROJECT, new SynchronizedBucket(new MemoryBucket()));
  }

  @Provides
  @BytecodeDb
  public FullPath provideBytecodeDb() {
    return BYTECODE_DB_PATH;
  }

  @Provides
  @ComputationDb
  public FullPath provideComputationDb() {
    return COMPUTATION_DB_PATH;
  }

  @Provides
  @Project
  public FullPath provideProject() {
    return PROJECT_PATH;
  }

  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash() {
    return Hash.of(33);
  }
}
