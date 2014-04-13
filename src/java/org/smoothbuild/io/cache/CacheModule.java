package org.smoothbuild.io.cache;

import static org.smoothbuild.io.fs.base.Path.path;

import javax.inject.Singleton;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.TaskResults;
import org.smoothbuild.io.cache.hash.Objects;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class CacheModule extends AbstractModule {
  public static final Path VALUE_DB_DIR = path("values");
  public static final Path TASK_DB_DIR = path("tasks");
  public static final Path RESULTS_DIR = path("results");

  @Override
  protected void configure() {}

  @Singleton
  @TaskResults
  @Provides
  public HashedDb provideTasksCache(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, TASK_DB_DIR);
    return new HashedDb(objectsFileSystem);
  }

  @Singleton
  @Objects
  @Provides
  public HashedDb provideValuesCache(@SmoothDir FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, VALUE_DB_DIR);
    return new HashedDb(objectsFileSystem);
  }
}
