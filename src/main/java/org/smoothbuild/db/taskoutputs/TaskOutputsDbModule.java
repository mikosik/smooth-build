package org.smoothbuild.db.taskoutputs;

import static org.smoothbuild.SmoothConstants.TASK_RESULTS_DIR;

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
  public HashedDb provideTaksOutputsHashedDb(@SmoothDir FileSystem fileSystem) {
    return new HashedDb(new SubFileSystem(fileSystem, TASK_RESULTS_DIR));
  }
}
