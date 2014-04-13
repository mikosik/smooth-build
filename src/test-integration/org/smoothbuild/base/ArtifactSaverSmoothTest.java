package org.smoothbuild.base;

import static org.smoothbuild.SmoothContants.ARTIFACTS_DIR;
import static org.smoothbuild.SmoothContants.SMOOTH_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.task.exec.save.err.DuplicatePathsInFileArrayArtifactError;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ArtifactSaverSmoothTest extends IntegrationTestCase {
  private static final Path RESULTS_PATH = SMOOTH_DIR.append(ARTIFACTS_DIR);

  Path path1 = path("def/filename1.txt");
  Path path2 = path("def/filename2.txt");
  String content1 = "some content";
  String content2 = "some content2";

  String functionName = "myFunction";

  // basic types

  @Test
  public void storing_string_artifact() throws Exception {
    // given
    script(functionName + " : '" + content1 + "' ;");

    // when
    build(functionName);

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  @Test
  public void storing_blob_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    script(functionName + " : file(" + path1 + ") | content ;");

    // when
    build(functionName);

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  @Test
  public void storing_file_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    script(functionName + " : file(" + path1 + ") ;");

    // when
    build(functionName);

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  // array types

  @Test
  public void storing_string_array_artifact() throws Exception {
    // given
    script(functionName + " : [ '" + content1 + "', '" + content2 + "' ]  ;");

    // when
    build(functionName);

    // then
    userConsole.messages().assertNoProblems();

    Path dirPath = RESULTS_PATH.append(path(functionName));
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

    script(functionName + " : [ content(file(" + path1 + ")) , content(file(" + path2 + ")) ] ;");

    // when
    build(functionName);

    // then
    userConsole.messages().assertNoProblems();

    Path dirPath = RESULTS_PATH.append(path(functionName));
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

    script(functionName + " : [ file(" + path1 + ") , file(" + path2 + ") ]  ;");

    // when
    build(functionName);

    // then
    userConsole.messages().assertNoProblems();

    Path dirPath = RESULTS_PATH.append(path(functionName));
    Path artifact1Path = dirPath.append(path1);
    Path artifact2Path = dirPath.append(path2);

    fileSystem.assertFileContains(artifact1Path, content1);
    fileSystem.assertFileContains(artifact2Path, content2);
  }

  @Test
  public void storing_file_array_artifact_logs_error_when_files_have_duplicated_paths()
      throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    String functionName = "myFunction";

    script(functionName + " : [ file(" + path1 + ") , file(" + path1 + ") ]  ;");

    // when
    build(functionName);

    // then
    userConsole.messages().assertContainsOnly(DuplicatePathsInFileArrayArtifactError.class);
  }
}
