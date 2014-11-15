package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class OverridingBuiltinFunctionIsForbiddenSmoothTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  @Test
  public void test() throws IOException {
    // given
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("file: 'abc';");
    builder.addLine("run: 'def';");
    script(fileSystem, builder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(OverridenBuiltinFunctionError.class);
  }
}
