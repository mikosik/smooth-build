package org.smoothbuild.lang.object.db;

import static org.smoothbuild.SmoothConstants.HASHED_DB_PATH;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ObjectsDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  public HashedDb provideHashedDb(FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, HASHED_DB_PATH, tempManager);
  }

  @Provides
  public ValuesDb provideValuesDb(HashedDb hashedDb) {
    return new ValuesDb(hashedDb);
  }

  @Provides
  @Singleton
  public ObjectsDb provideObjectsDb(ValuesDb valuesDb) {
    return new ObjectsDb(valuesDb);
  }
}
