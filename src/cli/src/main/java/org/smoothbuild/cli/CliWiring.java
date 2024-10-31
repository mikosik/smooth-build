package org.smoothbuild.cli;

import static org.smoothbuild.cli.layout.Layout.ARTIFACTS;
import static org.smoothbuild.cli.layout.Layout.BYTECODE_DB;
import static org.smoothbuild.cli.layout.Layout.COMPUTATION_DB;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.smoothbuild.cli.layout.InstallationHashes;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.wiring.BucketFactory;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Sandbox;
import org.smoothbuild.virtualmachine.wire.VmConfig;

public class CliWiring extends AbstractModule {
  private final Set<BucketId> bucketIds;

  public CliWiring(Set<BucketId> bucketIds) {
    this.bucketIds = bucketIds;
  }

  @Provides
  @Singleton
  public Map<BucketId, Bucket> provideBucketIdToBucketMap(BucketFactory bucketFactory) {
    return bucketIds.toMap(bucketFactory::create);
  }

  @Provides
  @Artifacts
  public FullPath provideArtifacts() {
    return ARTIFACTS;
  }

  @Provides
  @BytecodeDb
  public FullPath provideBytecodeDb() {
    return BYTECODE_DB;
  }

  @Provides
  @ComputationDb
  public FullPath provideComputationDb() {
    return COMPUTATION_DB;
  }

  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }

  @Provides
  public VmConfig provideVmConfig() {
    return new VmConfig(Layout.PROJECT, COMPUTATION_DB, BYTECODE_DB);
  }
}
