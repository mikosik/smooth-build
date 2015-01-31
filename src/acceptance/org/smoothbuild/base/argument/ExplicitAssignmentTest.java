package org.smoothbuild.base.argument;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.artifactPath;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;

import javax.inject.Inject;

import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.function.def.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.err.TypeMismatchError;
import org.smoothbuild.lang.function.def.err.UnknownParamNameError;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.acceptance.TestingFunctions.BlobIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.StringArrayIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.StringIdentity;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ExplicitAssignmentTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Test
  public void fails_when_parameter_does_not_exist() throws Exception {
    createInjector(new AcceptanceTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity(wrongName='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(UnknownParamNameError.class);
  }

  @Test
  public void fails_when_parameter_has_incompatible_type() throws Exception {
    createInjector(new AcceptanceTestModule(BlobIdentity.class)).injectMembers(this);
    script(fileSystem, "result : blobIdentity(blob='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(TypeMismatchError.class);
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    createInjector(new AcceptanceTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity(string='abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    createInjector(new AcceptanceTestModule(BlobIdentity.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : blobIdentity(blob=file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "file.txt");
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    createInjector(new AcceptanceTestModule(StringArrayIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringArrayIdentity(stringArray=[]) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    assertFalse(fileSystem.filesFrom(artifactPath("result")).iterator().hasNext());
  }

  @Test
  public void fails_when_two_arguments_are_assigned_to_same_parameter() throws Exception {
    createInjector(new AcceptanceTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity(string='abc', string='def') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(DuplicateArgNameError.class);
  }
}
