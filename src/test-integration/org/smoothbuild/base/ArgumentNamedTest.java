package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import javax.inject.Inject;

import org.junit.Test;
import org.smoothbuild.base.TestingFunctions.BlobIdentity;
import org.smoothbuild.base.TestingFunctions.StringArrayIdentity;
import org.smoothbuild.base.TestingFunctions.StringIdentity;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.function.def.args.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.args.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.args.err.UnknownParamNameError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ArgumentNamedTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  // one to one assignment

  @Test
  public void one_named_argument_and_one_parameter_with_given_type_but_different_name()
      throws Exception {
    createInjector(new IntegrationTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity(wrongName='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(UnknownParamNameError.class);
  }

  @Test
  public void one_named_argument_and_one_parameter_with_given_name_but_different_type()
      throws Exception {
    createInjector(new IntegrationTestModule(BlobIdentity.class)).injectMembers(this);
    script(fileSystem, "result : blobIdentity(blob='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(TypeMismatchError.class);
  }

  @Test
  public void one_named_argument_and_one_parameter_with_given_name_and_type() throws Exception {
    createInjector(new IntegrationTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity(string='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void one_argument_and_one_parameter_with_given_name_and_subtype() throws Exception {
    createInjector(new IntegrationTestModule(BlobIdentity.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : blobIdentity(blob=file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "file.txt");
  }

  @Test
  public void nil_argument_can_be_assigned_to_string_array_parameter() throws Exception {
    createInjector(new IntegrationTestModule(StringArrayIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringArrayIdentity(stringArray=[]) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    assertFalse(fileSystem.filesFrom(artifactPath("result")).iterator().hasNext());
  }

  // two arguments

  @Test
  public void two_arguments_with_the_same_name_causes_error() throws Exception {
    createInjector(new IntegrationTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity(string='abc', string='def') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(DuplicateArgNameError.class);
  }
}
