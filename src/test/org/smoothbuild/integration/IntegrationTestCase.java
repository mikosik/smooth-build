package org.smoothbuild.integration;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;

import java.io.IOException;

import org.junit.Before;
import org.smoothbuild.run.SmoothRunner;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.fs.base.TestFileSystemModule;
import org.smoothbuild.testing.parse.ScriptBuilder;
import org.smoothbuild.testing.problem.TestMessageListener;
import org.smoothbuild.testing.problem.TestMessageListenerModule;

import com.google.inject.Injector;

public class IntegrationTestCase {
  protected TestFileSystem fileSystem;
  protected SmoothRunner smoothRunner;
  protected TestMessageListener messages;

  @Before
  public void before() {
    Injector injector = createInjector(new TestFileSystemModule(),
        new TestMessageListenerModule());
    fileSystem = injector.getInstance(TestFileSystem.class);
    messages = injector.getInstance(TestMessageListener.class);
    smoothRunner = injector.getInstance(SmoothRunner.class);
  }

  public void script(String script) throws IOException {
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, ScriptBuilder.script(script));
  }

}
