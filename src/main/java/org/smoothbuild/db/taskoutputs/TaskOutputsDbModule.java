package org.smoothbuild.db.taskoutputs;

import static org.smoothbuild.SmoothContants.TASK_RESULTS_DIR;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TaskOutputsDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @TaskOutputs
  @Provides
  private FileSystem provideTaskOutputsFileSystem(@SmoothDir FileSystem fileSystem) {
    return new SubFileSystem(fileSystem, TASK_RESULTS_DIR);
  }

  @TaskOutputs
  @Provides
  public HashedDb provideTaksOutputsHashedDb(@TaskOutputs FileSystem fileSystem) {
    return new HashedDb(fileSystem);
  }
}
