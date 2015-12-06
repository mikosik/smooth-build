package org.smoothbuild.db.values;

import static org.smoothbuild.SmoothConstants.VALUES_DIR;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ValuesDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Values
  @Provides
  public HashedDb provideValuesHashedDb(@SmoothDir FileSystem fileSystem) {
    return new HashedDb(new SubFileSystem(fileSystem, VALUES_DIR));
  }
}
