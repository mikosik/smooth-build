package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.testing.TestingBucketId.PROJECT;
import static org.smoothbuild.common.testing.TestingFullPath.BYTECODE_DB_PATH;
import static org.smoothbuild.common.testing.TestingFullPath.COMPUTATION_DB_PATH;
import static org.smoothbuild.common.testing.TestingFullPath.PROJECT_PATH;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.SynchronizedBucket;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.log.report.ReportWiring;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VmConfig;
import org.smoothbuild.virtualmachine.wire.VmWiring;

public class TestingVmWiring extends AbstractModule {
  @Override
  protected void configure() {
    install(new VmWiring());
    install(new ReportWiring(new PrintWriter(System.out), (label, logs) -> true, INFO));
  }

  @Provides
  @Singleton
  public Map<BucketId, Bucket> provideBucketMap() {
    return map(PROJECT, new SynchronizedBucket(new MemoryBucket()));
  }

  @Provides
  public VmConfig provideVmConfig() {
    return new VmConfig(PROJECT_PATH, COMPUTATION_DB_PATH, BYTECODE_DB_PATH);
  }

  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash() {
    return Hash.of(33);
  }
}
