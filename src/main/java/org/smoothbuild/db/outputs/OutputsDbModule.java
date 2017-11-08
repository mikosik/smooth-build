package org.smoothbuild.db.outputs;

import static org.smoothbuild.SmoothConstants.OUTPUTS_DB_PATH;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class OutputsDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Outputs
  @Provides
  public HashedDb provideOutputsHashedDb(FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, OUTPUTS_DB_PATH, tempManager);
  }
}
