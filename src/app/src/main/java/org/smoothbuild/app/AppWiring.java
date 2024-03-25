package org.smoothbuild.app;

import static org.smoothbuild.app.layout.Layout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.app.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.app.layout.SmoothBucketId.PROJECT;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.smoothbuild.app.layout.InstallationHashes;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.BucketId;
import org.smoothbuild.common.bucket.base.SubBucket;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;

public class AppWiring extends AbstractModule {
  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }

  @Provides
  @ComputationDb
  public Bucket provideComputationCacheBucket(@Project Bucket bucket) {
    return new SubBucket(bucket, COMPUTATION_CACHE_PATH);
  }

  @Provides
  @BytecodeDb
  public Bucket provideBytecodeDbBucket(@Project Bucket bucket) {
    return new SubBucket(bucket, HASHED_DB_PATH);
  }

  @Provides
  @Project
  public Bucket provideProjectBucket(Map<BucketId, Bucket> map) {
    return map.get(PROJECT);
  }
}