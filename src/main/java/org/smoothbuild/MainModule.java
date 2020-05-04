package org.smoothbuild;

import static org.smoothbuild.install.InstallationPaths.installationPaths;

import org.smoothbuild.exec.task.TaskModule;
import org.smoothbuild.install.InstallationPaths;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.object.db.ObjectDbModule;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(InstallationPaths.class).toInstance(installationPaths());
    install(new TaskModule());
    install(new ObjectDbModule());
    install(new FileSystemModule());
  }
}
