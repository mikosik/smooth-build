package org.smoothbuild.integration;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.app.SmoothApp;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.fs.base.TestFileSystemModule;
import org.smoothbuild.testing.message.TestUserConsole;
import org.smoothbuild.testing.message.TestUserConsoleModule;
import org.smoothbuild.testing.parse.ScriptBuilder;
import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.testing.type.impl.TestFileSet;

import com.google.inject.Injector;

public class IntegrationTestCase {
  protected TestFileSystem fileSystem;
  protected SmoothApp smoothApp;
  protected TestUserConsole messages;

  @Before
  public void before() {
    reset();
  }

  protected void reset() {
    Injector injector = createInjector(new TestFileSystemModule(), new TestUserConsoleModule());
    fileSystem = injector.getInstance(TestFileSystem.class);
    messages = injector.getInstance(TestUserConsole.class);
    smoothApp = injector.getInstance(SmoothApp.class);
  }

  public void script(String script) throws IOException {
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

  protected TestFileSet fileSet(Path path) {
    return new TestFileSet(fileSystem.subFileSystem(path));
  }

  protected FakeFile file(Path path) {
    return new FakeFile(fileSystem, path);
  }
}
