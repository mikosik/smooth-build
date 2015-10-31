package org.smoothbuild.db.values;

import static org.smoothbuild.SmoothConstants.OBJECTS_DIR;

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
  public HashedDb provideObjectsHashedDb(@SmoothDir FileSystem fileSystem) {
    return new HashedDb(new SubFileSystem(fileSystem, OBJECTS_DIR));
  }
}
