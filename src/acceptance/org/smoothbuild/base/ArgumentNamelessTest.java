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
import org.smoothbuild.base.TestingFunctions.FileAndBlob;
import org.smoothbuild.base.TestingFunctions.StringArrayIdentity;
import org.smoothbuild.base.TestingFunctions.StringIdentity;
import org.smoothbuild.base.TestingFunctions.TwoBlobs;
import org.smoothbuild.base.TestingFunctions.TwoStrings;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.function.def.err.AmbiguousNamelessArgsError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ArgumentNamelessTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  // simple mapping

  @Test
  public void one_nameless_argument_and_zero_parameters_with_same_type() throws Exception {
    createInjector(new IntegrationTestModule(BlobIdentity.class)).injectMembers(this);
    script(fileSystem, "result : blobIdentity('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void one_nameless_argument_and_one_parameter_with_same_type() throws Exception {
    createInjector(new IntegrationTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void two_nameless_arguments_and_one_parameter_with_the_same_type() throws Exception {
    createInjector(new IntegrationTestModule(StringIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringIdentity('abc', 'def') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void one_nameless_argument_and_two_parameters_with_same_type() throws Exception {
    createInjector(new IntegrationTestModule(TwoStrings.class)).injectMembers(this);
    script(fileSystem, "result : twoStrings('abc') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void two_nameless_arguments_and_two_parameters_with_same_type() throws Exception {
    createInjector(new IntegrationTestModule(TwoStrings.class)).injectMembers(this);
    script(fileSystem, "result : twoStrings('abc', 'def') ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void two_nameless_arguments_one_with_type_one_with_subtype_and_two_parameters_one_with_type_one_with_subtype()
      throws Exception {
    createInjector(new IntegrationTestModule(FileAndBlob.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file1.txt"));
    fileSystem.createFileContainingItsPath(path("file2.txt"));
    script(fileSystem, "result : fileAndBlob(file('file1.txt'), content(file('file2.txt'))) ;");

    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "file1.txt:file2.txt");
  }

  // mapping with implicit conversion

  @Test
  public void one_nameless_argument_and_one_parameter_with_subtype() throws Exception {
    createInjector(new IntegrationTestModule(BlobIdentity.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : blobIdentity(file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "file.txt");
  }

  @Test
  public void one_nameless_argument_and_two_parameters_with_subtype() throws Exception {
    createInjector(new IntegrationTestModule(TwoBlobs.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : twoBlobs(file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void two_nameless_arguments_and_one_parameter_with_subtype() throws Exception {
    createInjector(new IntegrationTestModule(BlobIdentity.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : blobIdentity(file('file.txt'), file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  @Test
  public void two_nameless_arguments_and_two_parameters_one_with_subtype_and_one_with_type()
      throws Exception {
    createInjector(new IntegrationTestModule(TwoBlobs.class)).injectMembers(this);
    fileSystem.createFileContainingItsPath(path("file.txt"));
    script(fileSystem, "result : twoBlobs(file('file.txt'), file('file.txt')) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(AmbiguousNamelessArgsError.class);
  }

  // arrays

  @Test
  public void nil_is_mapped_to_string_array() throws Exception {
    createInjector(new IntegrationTestModule(StringArrayIdentity.class)).injectMembers(this);
    script(fileSystem, "result : stringArrayIdentity([]) ;");

    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    assertFalse(fileSystem.filesFrom(artifactPath("result")).iterator().hasNext());
  }
}
