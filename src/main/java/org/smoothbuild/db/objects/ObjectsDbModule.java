package org.smoothbuild.db.objects;

import static org.smoothbuild.SmoothConstants.OBJECTS_DIR;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ObjectsDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Objects
  @Provides
  public HashedDb provideObjectsHashedDb(@SmoothDir FileSystem fileSystem) {
    return new HashedDb(new SubFileSystem(fileSystem, OBJECTS_DIR));
  }
}
