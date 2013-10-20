package org.smoothbuild.integration;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.app.SmoothApp;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.fs.base.FakeFileSystemModule;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.message.FakeUserConsoleModule;
import org.smoothbuild.testing.parse.ScriptBuilder;
import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.testing.type.impl.FakeFileSet;

import com.google.inject.Injector;

public class IntegrationTestCase {
  protected FakeFileSystem fileSystem;
  protected SmoothApp smoothApp;
  protected FakeUserConsole messages;

  @Before
  public void before() {
    reset();
  }

  protected void reset() {
    Injector injector = createInjector(new FakeFileSystemModule(), new FakeUserConsoleModule());
    fileSystem = injector.getInstance(FakeFileSystem.class);
    messages = injector.getInstance(FakeUserConsole.class);
    smoothApp = injector.getInstance(SmoothApp.class);
  }

  public void script(String script) throws IOException {
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

  protected FakeFileSet fileSet(Path path) {
    return new FakeFileSet(fileSystem.subFileSystem(path));
  }

  protected FakeFile file(Path path) {
    return new FakeFile(fileSystem, path);
  }
}
