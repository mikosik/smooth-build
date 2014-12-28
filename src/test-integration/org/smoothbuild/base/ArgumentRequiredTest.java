package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import javax.inject.Inject;

import org.junit.Test;
import org.smoothbuild.base.TestingFunctions.OneOptionalOneRequired;
import org.smoothbuild.base.TestingFunctions.OneRequired;
import org.smoothbuild.base.TestingFunctions.TwoRequired;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.function.def.args.err.MissingRequiredArgsError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ArgumentRequiredTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Test
  public void missing_required_parameter_causes_error() throws Exception {
    createInjector(new IntegrationTestModule(OneRequired.class)).injectMembers(this);
    script(fileSystem, "result : oneRequired() ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(MissingRequiredArgsError.class);
  }

  @Test
  public void assigned_required_parameter() throws Exception {
    createInjector(new IntegrationTestModule(OneRequired.class)).injectMembers(this);
    script(fileSystem, "result : oneRequired(string='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void assigned_required_parameter_from_nameless_argument() throws Exception {
    createInjector(new IntegrationTestModule(OneRequired.class)).injectMembers(this);
    script(fileSystem, "result : oneRequired('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void one_missing_required_argument_out_of_two_causes_error() throws Exception {
    createInjector(new IntegrationTestModule(TwoRequired.class)).injectMembers(this);
    script(fileSystem, "result : twoRequired(stringA='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(MissingRequiredArgsError.class);
  }

  @Test
  public void nameless_argument_with_type_matching_two_parameters_is_assigned_two_required_one()
      throws Exception {
    createInjector(new IntegrationTestModule(OneOptionalOneRequired.class)).injectMembers(this);
    script(fileSystem, "result : oneOptionalOneRequired('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc:null");
  }
}
