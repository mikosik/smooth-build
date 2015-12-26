package org.smoothbuild.db.values;

import static org.smoothbuild.SmoothConstants.VALUES_DB_PATH;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.FileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ValuesDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Values
  @Provides
  public HashedDb provideValuesHashedDb(FileSystem fileSystem) {
    return new HashedDb(fileSystem, VALUES_DB_PATH);
  }
}
