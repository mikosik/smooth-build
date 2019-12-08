package org.smoothbuild;

import org.smoothbuild.exec.TaskModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.object.db.ObjectDbModule;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  private final SmoothPaths smoothPaths;

  public MainModule(SmoothPaths smoothPaths) {
    this.smoothPaths = smoothPaths;
  }

  @Override
  protected void configure() {
    bind(SmoothPaths.class).toInstance(smoothPaths);
    install(new TaskModule());
    install(new ObjectDbModule());
    install(new FileSystemModule());
  }
}
