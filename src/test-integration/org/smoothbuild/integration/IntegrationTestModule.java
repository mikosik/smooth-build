package org.smoothbuild.integration;

import org.smoothbuild.object.ObjectModule;
import org.smoothbuild.testing.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.inject.AbstractModule;

public class IntegrationTestModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new ObjectModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }
}
