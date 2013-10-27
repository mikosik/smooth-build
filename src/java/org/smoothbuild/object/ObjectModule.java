package org.smoothbuild.object;

import static org.smoothbuild.command.SmoothContants.TASK_RESULT_DIR;
import static org.smoothbuild.command.SmoothContants.OBJECTS_DIR;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ObjectModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Singleton
  @TaskResults
  @Provides
  public HashedDb taskResultHashedDb(FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, TASK_RESULT_DIR);
    return new HashedDb(objectsFileSystem);
  }

  @Singleton
  @Objects
  @Provides
  public HashedDb objectHashedDb(FileSystem fileSystem) {
    FileSystem objectsFileSystem = new SubFileSystem(fileSystem, OBJECTS_DIR);
    return new HashedDb(objectsFileSystem);
  }
}
