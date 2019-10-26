package org.smoothbuild;

import org.smoothbuild.db.values.ValuesDbModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.type.TypesModule;
import org.smoothbuild.task.TaskModule;

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
    install(new TypesModule());
    install(new ValuesDbModule());
    install(new FileSystemModule());
  }
}
