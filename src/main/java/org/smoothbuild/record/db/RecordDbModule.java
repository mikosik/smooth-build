package org.smoothbuild.record.db;

import static org.smoothbuild.install.ProjectPaths.RECORD_DB_PATH;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.util.TempManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class RecordDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  public HashedDb provideHashedDb(FileSystem fileSystem, TempManager tempManager) {
    return new HashedDb(fileSystem, RECORD_DB_PATH, tempManager);
  }

  @Provides
  @Singleton
  public RecordDb provideRecordDb(HashedDb hashedDb) {
    return RecordDb.recordDb(hashedDb);
  }
}
