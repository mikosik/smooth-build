package org.smoothbuild.app.wire;

import static org.smoothbuild.app.layout.Layout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.app.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.app.layout.SmoothSpace.PROJECT;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.smoothbuild.app.layout.ForSpace;
import org.smoothbuild.app.layout.InstallationHashes;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.SubFileSystem;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;
import org.smoothbuild.virtualmachine.wire.Project;
import org.smoothbuild.virtualmachine.wire.Sandbox;

public class AppModule extends AbstractModule {
  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }

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
  @Project
  public FileSystem provideProjectFileSystem(@ForSpace(PROJECT) FileSystem fileSystem) {
    return fileSystem;
  }
}
