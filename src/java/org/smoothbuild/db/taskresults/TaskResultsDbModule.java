package org.smoothbuild.db.taskresults;

import static org.smoothbuild.SmoothContants.TASK_RESULTS_DIR;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.SubFileSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TaskResultsDbModule extends AbstractModule {
  @Override
  protected void configure() {}

  @TaskResults
  @Provides
  private FileSystem provideTaskResultsFileSystem(@SmoothDir FileSystem fileSystem) {
    return new SubFileSystem(fileSystem, TASK_RESULTS_DIR);
  }

  @TaskResults
  @Provides
  public HashedDb provideTaksResultsHashedDb(@TaskResults FileSystem fileSystem) {
    return new HashedDb(fileSystem);
  }
}
