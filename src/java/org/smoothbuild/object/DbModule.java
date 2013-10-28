package org.smoothbuild.object;

import static org.smoothbuild.command.SmoothContants.TASK_RESULT_DIR;
import static org.smoothbuild.command.SmoothContants.VALUE_DB_DIR;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Singleton
  @Results
  @Provides
  public HashedDb taskResultHashedDb(FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, TASK_RESULT_DIR);
    return new HashedDb(objectsFileSystem);
  }

  @Singleton
  @HashedDbWithValues
  @Provides
  public HashedDb objectHashedDb(FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, VALUE_DB_DIR);
    return new HashedDb(objectsFileSystem);
  }
}
