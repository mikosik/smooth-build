package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.IoConstants.SMOOTH_DIR;
import static org.smoothbuild.io.cache.CacheModule.RESULTS_DIR;
import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class StoringArtifactSmoothTest extends IntegrationTestCase {
  private static final Path RESULTS_PATH = SMOOTH_DIR.append(RESULTS_DIR);

  Path path1 = path("def/filename1.txt");
  Path path2 = path("def/filename2.txt");
  String content1 = "some content";
  String content2 = "some content2";

  String functionName = "myFunction";

  @Test
  public void storing_file_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    script(functionName + " : file(" + path1 + ") ;");

    // when
    build(functionName);

    // then
    userConsole.assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  @Test
  public void storing_file_set_artifact() throws Exception {
    // given
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    String functionName = "myFunction";

    script(functionName + " : [ file(" + path1 + ") , file(" + path2 + ") ]  ;");

    // when
    build(functionName);

    // then
    userConsole.assertNoProblems();

    Path dirPath = RESULTS_PATH.append(path(functionName));
    Path artifact1Path = dirPath.append(path1);
    Path artifact2Path = dirPath.append(path2);

    fileSystem.assertFileContains(artifact1Path, content1);
    fileSystem.assertFileContains(artifact2Path, content2);
  }

  @Test
  public void storing_string_artifact() throws Exception {
    // given
    script(functionName + " : '" + content1 + "' ;");

    // when
    build(functionName);

    // then
    userConsole.assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path(functionName));
    fileSystem.assertFileContains(artifactPath, content1);
  }

  @Test
  public void storing_string_set_artifact() throws Exception {
    // given
    String functionName = "myFunction";

    script(functionName + " : [ '" + content1 + "', '" + content2 + "' ]  ;");

    // when
    build(functionName);

    // then
    userConsole.assertNoProblems();

    Path dirPath = RESULTS_PATH.append(path(functionName));
    Path artifact1Path = dirPath.append(path("0"));
    Path artifact2Path = dirPath.append(path("1"));

    fileSystem.assertFileContains(artifact1Path, content1);
    fileSystem.assertFileContains(artifact2Path, content2);
  }
}
