package org.smoothbuild.io.db;

import static org.smoothbuild.io.fs.base.Path.path;

import javax.inject.Singleton;

import org.smoothbuild.io.db.hash.HashedDb;
import org.smoothbuild.io.db.hash.TasksCache;
import org.smoothbuild.io.db.hash.ValuesCache;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class DbModule extends AbstractModule {
  public static final Path VALUE_DB_DIR = path("values");
  public static final Path TASK_DB_DIR = path("tasks");
  public static final Path RESULTS_DIR = path("results");

  @Override
  protected void configure() {}

  @Singleton
  @TasksCache
  @Provides
  public HashedDb provideTasksCache(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, TASK_DB_DIR);
    return new HashedDb(objectsFileSystem);
  }

  @Singleton
  @ValuesCache
  @Provides
  public HashedDb provideValuesCache(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, VALUE_DB_DIR);
    return new HashedDb(objectsFileSystem);
  }
}
