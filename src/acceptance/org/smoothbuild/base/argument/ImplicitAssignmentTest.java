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
import org.smoothbuild.lang.function.def.err.AmbiguousNamelessArgsError;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.acceptance.TestingFunctions.BlobIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.FileAndBlob;
import org.smoothbuild.testing.acceptance.TestingFunctions.StringArrayIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.StringIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.TwoBlobs;
import org.smoothbuild.testing.acceptance.TestingFunctions.TwoStrings;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ImplicitAssignmentTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Test
  public void fails_when_there_is_no_parameter_matching() throws Exception {
    createInjector(new AcceptanceTestModule(BlobIdentity.class)).injectMembers(this);
    script(fileSystem, "result : blobIdentity('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    createInjector(new AcceptanceTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    createInjector(new AcceptanceTestModule(BlobIdentity.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : blobIdentity(file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "file.txt");
  }

  @Test
  public void fails_when_one_parameter_matches_two_arguments() throws Exception {
    createInjector(new AcceptanceTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity('abc', 'def') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void fails_when_two_parameters_match_argument() throws Exception {
    createInjector(new AcceptanceTestModule(TwoStrings.class)).injectMembers(this);
    script(fileSystem, "result : twoStrings('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void fails_when_two_parameters_match_two_arguments() throws Exception {
    createInjector(new AcceptanceTestModule(TwoStrings.class)).injectMembers(this);
    script(fileSystem, "result : twoStrings('abc', 'def') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void assigns_most_specific_type_first() throws Exception {
    createInjector(new AcceptanceTestModule(FileAndBlob.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file1.txt"));
    fileSystem.createFileContainingItsPath(path("file2.txt"));
    script(fileSystem, "result : fileAndBlob(file('file1.txt'), content(file('file2.txt'))) ;");

    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "file1.txt:file2.txt");
  }

  @Test
  public void fails_when_argument_matches_two_parameters_with_supertype() throws Exception {
    createInjector(new AcceptanceTestModule(TwoBlobs.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : twoBlobs(file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void fails_when_two_arguments_match_parameter_with_supertype() throws Exception {
    createInjector(new AcceptanceTestModule(BlobIdentity.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : blobIdentity(file('file.txt'), file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void fails_when_two_arguments_match_parameter_and_other_parameter_with_supertype()
      throws Exception {
    createInjector(new AcceptanceTestModule(TwoBlobs.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : twoBlobs(file('file.txt'), file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  // arrays

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    createInjector(new AcceptanceTestModule(StringArrayIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringArrayIdentity([]) ;");

    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    assertFalse(fileSystem.filesFrom(artifactPath("result")).iterator().hasNext());
  }
}
