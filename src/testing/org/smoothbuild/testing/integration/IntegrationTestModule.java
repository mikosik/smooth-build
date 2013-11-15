package org.smoothbuild.testing.integration;

import org.smoothbuild.builtin.BuiltinModule;
import org.smoothbuild.io.db.DbModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.inject.AbstractModule;

public class IntegrationTestModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new DbModule());
    install(new BuiltinModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }
}
