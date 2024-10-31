package org.smoothbuild.cli;

import static org.smoothbuild.cli.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.cli.layout.Layout.BYTECODE_DB_PATH;
import static org.smoothbuild.cli.layout.Layout.COMPUTATION_DB_PATH;
import static org.smoothbuild.cli.layout.Layout.PROJECT_PATH;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.smoothbuild.cli.layout.InstallationHashes;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Alias;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.bucket.wiring.BucketFactory;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;

public class CliWiring extends AbstractModule {
  private final Set<Alias> aliases;

  public CliWiring(Set<Alias> aliases) {
    this.aliases = aliases;
  }

  @Provides
  @Singleton
  public Map<Alias, Bucket> provideAliasToBucketMap(BucketFactory bucketFactory) {
    return aliases.toMap(bucketFactory::create);
  }

  @Provides
  @Artifacts
  public FullPath provideArtifacts() {
    return ARTIFACTS_PATH;
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
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }
}
