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
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ParameterTest {
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
  public void trailing_comma_is_allowed_in_parameter_list() throws IOException {
    // given
    script(fileSystem, "run : toBlob(string='abc',) ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
  }
}
