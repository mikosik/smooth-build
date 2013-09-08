package org.smoothbuild.integration;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.run.SmoothRunner;
import org.smoothbuild.testing.ScriptBuilder;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.fs.base.TestFileSystemModule;
import org.smoothbuild.testing.problem.TestingProblemsListener;
import org.smoothbuild.testing.problem.TestingProblemsListenerModule;

import com.google.inject.Injector;

public class IntegrationTestCase {
  protected TestFileSystem fileSystem;
  protected SmoothRunner smoothRunner;
  protected TestingProblemsListener problems;

  @Before
  public void before() {
    Injector injector = createInjector(new TestFileSystemModule(),
        new TestingProblemsListenerModule());
    fileSystem = injector.getInstance(TestFileSystem.class);
    problems = injector.getInstance(TestingProblemsListener.class);
    smoothRunner = injector.getInstance(SmoothRunner.class);
  }

  public void script(String script) throws IOException {
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

}
