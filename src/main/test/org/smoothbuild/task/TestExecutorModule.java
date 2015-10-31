package org.smoothbuild.task;

import javax.inject.Singleton;

import org.smoothbuild.db.outputs.OutputsDbModule;
import org.smoothbuild.db.values.ValuesDbModule;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.SmoothDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.SmoothJar;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskReporter;

import com.google.common.hash.HashCode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TestExecutorModule extends AbstractModule {

  @Override
  protected void configure() {
    install(new ValuesDbModule());
    install(new OutputsDbModule());
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
  @SmoothJar
  public HashCode provideSmoothJarHash() {
    return HashCode.fromInt(1);
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
    public void report(Task task, boolean resultFromCache) {}
  }
}
