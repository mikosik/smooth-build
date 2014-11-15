package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
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

public class ArtifactSaverSmoothTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  Path path1 = path("def/filename1.txt");
  Path path2 = path("def/filename2.txt");
  String content1 = "some content";
  String content2 = "some content2";

  String functionName = "myFunction";

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  // basic types

  @Test
  public void storing_string_artifact() throws Exception {
    // given
    script(fileSystem, functionName + " : '" + content1 + "' ;");

    // when
    buildWorker.run(asList(functionName));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  @Test
  public void storing_blob_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    script(fileSystem, functionName + " : file(" + path1 + ") | content ;");

    // when
    buildWorker.run(asList(functionName));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  @Test
  public void storing_file_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    script(fileSystem, functionName + " : file(" + path1 + ") ;");

    // when
    buildWorker.run(asList(functionName));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  // array types

  @Test
  public void storing_string_array_artifact() throws Exception {
    // given
    script(fileSystem, functionName + " : [ '" + content1 + "', '" + content2 + "' ]  ;");

    // when
    buildWorker.run(asList(functionName));

    // then
    userConsole.messages().assertNoProblems();

    Path dirPath = ARTIFACTS_PATH.append(path(functionName));
    Path artifact1Path = dirPath.append(path("0"));
    Path artifact2Path = dirPath.append(path("1"));

    fileSystem.assertFileContains(artifact1Path, content1);
    fileSystem.assertFileContains(artifact2Path, content2);
  }

  @Test
  public void storing_blob_array_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    String functionName = "myFunction";

    script(fileSystem,
        functionName + " : [ content(file(" + path1 + ")) , content(file(" + path2 + ")) ] ;");

    // when
    buildWorker.run(asList(functionName));

    // then
    userConsole.messages().assertNoProblems();

    Path dirPath = ARTIFACTS_PATH.append(path(functionName));
    Path artifact1Path = dirPath.append(path("0"));
    Path artifact2Path = dirPath.append(path("1"));

    fileSystem.assertFileContains(artifact1Path, content1);
    fileSystem.assertFileContains(artifact2Path, content2);
  }

  @Test
  public void storing_file_array_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    String functionName = "myFunction";

    script(fileSystem, functionName + " : [ file(" + path1 + ") , file(" + path2 + ") ]  ;");

    // when
    buildWorker.run(asList(functionName));

    // then
    userConsole.messages().assertNoProblems();

    Path dirPath = ARTIFACTS_PATH.append(path(functionName));
    Path artifact1Path = dirPath.append(path1);
    Path artifact2Path = dirPath.append(path2);

    fileSystem.assertFileContains(artifact1Path, content1);
    fileSystem.assertFileContains(artifact2Path, content2);
  }

  @Test
  public void storing_file_array_artifact_logs_error_when_files_have_duplicated_paths() throws
      Exception {
    // given
    fileSystem.createFile(path1, content1);

    String functionName = "myFunction";

    script(fileSystem, functionName + " : [ file(" + path1 + ") , file(" + path1 + ") ]  ;");

    // when
    buildWorker.run(asList(functionName));

    // then
    userConsole.messages().assertContainsOnly(DuplicatePathsInFileArrayArtifactError.class);
  }
}
