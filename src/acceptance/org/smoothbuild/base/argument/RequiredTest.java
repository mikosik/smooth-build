package org.smoothbuild.base.argument;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;

import javax.inject.Inject;

import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.function.def.err.MissingRequiredArgsError;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.acceptance.TestingFunctions.OneRequired;
import org.smoothbuild.testing.acceptance.TestingFunctions.TwoRequired;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class RequiredTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Test
  public void fails_when_required_parameter_is_missing() throws Exception {
    createInjector(new AcceptanceTestModule(OneRequired.class)).injectMembers(this);
    script(fileSystem, "result : oneRequired() ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(MissingRequiredArgsError.class);
  }

  @Test
  public void fails_when_only_one_out_of_two_required_parameters_is_present() throws Exception {
    createInjector(new AcceptanceTestModule(TwoRequired.class)).injectMembers(this);
    script(fileSystem, "result : twoRequired(stringA='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(MissingRequiredArgsError.class);
  }
}
