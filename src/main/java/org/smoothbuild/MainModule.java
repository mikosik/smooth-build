package org.smoothbuild;

import org.smoothbuild.exec.task.TaskModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.object.db.ObjectDbModule;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(SmoothPaths.class).toInstance(SmoothPaths.smoothPaths());
    install(new TaskModule());
    install(new ObjectDbModule());
    install(new FileSystemModule());
  }
}
