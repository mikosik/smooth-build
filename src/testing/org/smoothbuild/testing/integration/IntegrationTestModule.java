package org.smoothbuild.testing.integration;

import org.smoothbuild.io.cache.CacheModule;
import org.smoothbuild.lang.builtin.BuiltinModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsoleModule;

import com.google.inject.AbstractModule;

public class IntegrationTestModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new CacheModule());
    install(new BuiltinModule());
    install(new FakeFileSystemModule());
    install(new FakeUserConsoleModule());
  }
}
