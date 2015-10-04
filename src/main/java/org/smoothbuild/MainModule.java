package org.smoothbuild;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.io.util.ReleaseJarModule;
import org.smoothbuild.lang.module.ModuleModule;
import org.smoothbuild.message.base.MessageModule;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskOutputsDbModule());
    install(new ObjectsDbModule());
    install(new FileSystemModule());
    install(new ModuleModule());
    install(new ReleaseJarModule());
    install(new MessageModule());
  }
}
