package org.smoothbuild.testing.integration;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskoutputs.TaskOutputsDbModule;
import org.smoothbuild.lang.builtin.BuiltinModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.inject.AbstractModule;

public class IntegrationTestModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskOutputsDbModule());
    install(new ObjectsDbModule());
    install(new BuiltinModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }
}
