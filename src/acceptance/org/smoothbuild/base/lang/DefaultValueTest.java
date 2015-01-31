package org.smoothbuild.base.lang;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertFalse;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.artifactPath;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;

import javax.inject.Inject;

import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.acceptance.TestingFunctions.BlobIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.FileIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.StringArrayIdentity;
import org.smoothbuild.testing.acceptance.TestingFunctions.StringIdentity;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class DefaultValueTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Test
  public void default_value_for_string_is_empty_string() throws Exception {
    createInjector(new AcceptanceTestModule(StringIdentity.class)).injectMembers(this);

    script(fileSystem, "result : stringIdentity() ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "");
  }

  @Test
  public void default_value_for_blob_is_empty_stream() throws Exception {
    createInjector(new AcceptanceTestModule(BlobIdentity.class)).injectMembers(this);

    script(fileSystem, "result : blobIdentity() ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "");
  }

  @Test
  public void default_value_for_file_has_empty_path() throws Exception {
    createInjector(new AcceptanceTestModule(FileIdentity.class)).injectMembers(this);

    script(fileSystem, "result : fileIdentity() | path ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), ".");
  }

  @Test
  public void default_value_for_file_has_empty_content() throws Exception {
    createInjector(new AcceptanceTestModule(FileIdentity.class)).injectMembers(this);

    script(fileSystem, "result : fileIdentity() | content ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "");
  }

  @Test
  public void default_value_for_array_is_empty_array() throws Exception {
    createInjector(new AcceptanceTestModule(StringArrayIdentity.class)).injectMembers(this);

    script(fileSystem, "result : stringArrayIdentity() ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    assertFalse(fileSystem.filesFrom(artifactPath("result")).iterator().hasNext());
  }
}
