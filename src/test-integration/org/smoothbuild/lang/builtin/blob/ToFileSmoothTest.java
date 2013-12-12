package org.smoothbuild.lang.builtin.blob;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ToFileSmoothTest extends IntegrationTestCase {

  @Test
  public void test() throws IOException {
    String content = "file content";
    Path sourcePath = path("source/path/file.txt");
    Path destinationPath = path("destination/path/file.txt");

    fileSystem.createFile(sourcePath, content);

    script("run : [ toFile(path=" + destinationPath + ", content=file(" + sourcePath + ")) ];");

    build("run");

    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContains(artifactPath.append(destinationPath), content);
  }
}
