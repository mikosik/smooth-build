package org.smoothbuild.db;

import static org.smoothbuild.SmoothContants.OBJECTS_DIR;
import static org.smoothbuild.SmoothContants.TASK_RESULTS_DIR;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @TaskResults
  @Provides
  public HashedDb provideTasksCache(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, TASK_RESULTS_DIR);
    return new HashedDb(objectsFileSystem);
  }

  @Objects
  @Provides
  public HashedDb provideValuesCache(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, OBJECTS_DIR);
    return new HashedDb(objectsFileSystem);
  }
}
