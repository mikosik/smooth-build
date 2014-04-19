package org.smoothbuild.testing.integration;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskresults.TaskResultsDbModule;
import org.smoothbuild.lang.builtin.BuiltinModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.inject.AbstractModule;

public class IntegrationTestModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskResultsDbModule());
    install(new ObjectsDbModule());
    install(new BuiltinModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }
}
