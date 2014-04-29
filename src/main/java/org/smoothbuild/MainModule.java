package org.smoothbuild;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.io.fs.FileSystemModule;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskOutputsDbModule());
    install(new ObjectsDbModule());
    install(new FileSystemModule());
    install(createBuiltinModule());
  }

  /**
   * Workaround: Class BuiltinModule that provides BuiltinSmoothModule with core
   * smooth functions is placed in different source folder than main smooth so
   * we cannot reference it directly. After a few refactorings when plugin
   * system is in place that builtin module will be placed in proper directory
   * along smooth.jar and will be loaded automatically.
   */
  private Module createBuiltinModule() {
    try {
      return (Module) Class.forName("org.smoothbuild.builtin.BuiltinModule").newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
