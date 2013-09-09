package org.smoothbuild.integration;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.run.SmoothRunner;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.fs.base.TestFileSystemModule;
import org.smoothbuild.testing.parse.ScriptBuilder;
import org.smoothbuild.testing.problem.TestProblemsListener;
import org.smoothbuild.testing.problem.TestProblemsListenerModule;

import com.google.inject.Injector;

public class IntegrationTestCase {
  protected TestFileSystem fileSystem;
  protected SmoothRunner smoothRunner;
  protected TestProblemsListener problems;

  @Before
  public void before() {
    Injector injector = createInjector(new TestFileSystemModule(),
        new TestProblemsListenerModule());
    fileSystem = injector.getInstance(TestFileSystem.class);
    problems = injector.getInstance(TestProblemsListener.class);
    smoothRunner = injector.getInstance(SmoothRunner.class);
  }

  public void script(String script) throws IOException {
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

}
