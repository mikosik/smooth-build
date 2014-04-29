package org.smoothbuild.task;

import javax.inject.Singleton;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskReporter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TestExecutorModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new ObjectsDbModule());
    install(new TaskOutputsDbModule());
  }

  @Provides
  @Singleton
  @SmoothDir
  public FileSystem provideSmoothFileSystem() {
    return new MemoryFileSystem();
  }

  @Provides
  @Singleton
  @ProjectDir
  public FileSystem provideProjectFileSystem() {
    return new MemoryFileSystem();
  }

  @Provides
  public TaskReporter provideTaskReporter() {
    return new DummyTaskReporter();
  }

  private static class DummyTaskReporter extends TaskReporter {
    public DummyTaskReporter() {
      super(null);
    }

    @Override
    public void report(Task<?> task, boolean resultFromCache) {}
  }
}
