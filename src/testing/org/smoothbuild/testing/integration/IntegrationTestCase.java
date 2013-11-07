package org.smoothbuild.testing.integration;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.app.SmoothApp;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

public class IntegrationTestCase extends AbstractModule {
  protected FakeFileSystem fileSystem;
  protected SmoothApp smoothApp;
  protected FakeUserConsole userConsole;

  @Before
  public void before() {
    Module module = Modules.override(new IntegrationTestModule()).with(this);
    Injector injector = createInjector(module);
    fileSystem = injector.getInstance(FakeFileSystem.class);
    userConsole = injector.getInstance(FakeUserConsole.class);
    smoothApp = injector.getInstance(SmoothApp.class);
  }

  public void script(String script) throws IOException {
    fileSystem.createFile(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

  @Override
  protected void configure() {}
}
