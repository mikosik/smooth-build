package org.smoothbuild.db.object;

import static org.smoothbuild.install.ProjectPaths.OBJECT_DB_PATH;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ObjectDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  public ObjectDb provideObjectDb(FileSystem fileSystem, TempManager tempManager) {
    HashedDb hashedDb = new HashedDb(fileSystem, OBJECT_DB_PATH, tempManager);
    return ObjectDb.objectDb(hashedDb);
  }
}
