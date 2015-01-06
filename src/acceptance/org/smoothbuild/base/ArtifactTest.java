package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.task.save.err.DuplicatePathsInFileArrayArtifactError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ArtifactTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  private final Path path1 = path("def/filename1.txt");
  private final Path path2 = path("def/filename2.txt");
  private final String content1 = "some content";
  private final String content2 = "some content2";

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  // basic types

  @Test
  public void storing_string_artifact() throws Exception {
    script(fileSystem, "result : '" + content1 + "' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), content1);
  }

  @Test
  public void storing_blob_artifact() throws Exception {
    fileSystem.createFile(path1, content1);
    script(fileSystem, "result : file(" + path1 + ") | content ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), content1);
  }

  @Test
  public void storing_file_artifact() throws Exception {
    fileSystem.createFile(path1, content1);
    script(fileSystem, "result : file(" + path1 + ") ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), content1);
  }

  // array types

  @Test
  public void storing_string_array_artifact() throws Exception {
    script(fileSystem, "result : [ '" + content1 + "', '" + content2 + "' ]  ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();

    fileSystem.assertFileContains(artifactPath("result").append(path("0")), content1);
    fileSystem.assertFileContains(artifactPath("result").append(path("1")), content2);
  }

  @Test
  public void storing_blob_array_artifact() throws Exception {
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    script(fileSystem, "result : [ content(file(" + path1 + ")) , content(file(" + path2 + ")) ] ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result").append(path("0")), content1);
    fileSystem.assertFileContains(artifactPath("result").append(path("1")), content2);
  }

  @Test
  public void storing_file_array_artifact() throws Exception {
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    script(fileSystem, "result : [ file(" + path1 + ") , file(" + path2 + ") ]  ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result").append(path1), content1);
    fileSystem.assertFileContains(artifactPath("result").append(path2), content2);
  }

  @Test
  public void storing_file_array_artifact_logs_error_when_files_have_duplicated_paths()
      throws Exception {
    fileSystem.createFile(path1, content1);

    script(fileSystem, "result : [ file(" + path1 + ") , file(" + path1 + ") ]  ;");

    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(DuplicatePathsInFileArrayArtifactError.class);
  }
}
