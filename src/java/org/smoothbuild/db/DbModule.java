package org.smoothbuild.db;

import static org.smoothbuild.fs.base.Path.path;

import javax.inject.Singleton;

import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.db.hash.HashedDbWithTasks;
import org.smoothbuild.db.hash.HashedDbWithValues;
import org.smoothbuild.fs.SmoothDir;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DbModule extends AbstractModule {
  private static final Path VALUE_DB_DIR = path("values");
  private static final Path TASK_DB_DIR = path("tasks");

  @Override
  protected void configure() {}

  @Singleton
  @HashedDbWithTasks
  @Provides
  public HashedDb taskResultHashedDb(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, TASK_DB_DIR);
    return new HashedDb(objectsFileSystem);
  }

  @Singleton
  @HashedDbWithValues
  @Provides
  public HashedDb valuesHashedDb(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, VALUE_DB_DIR);
    return new HashedDb(objectsFileSystem);
  }
}
