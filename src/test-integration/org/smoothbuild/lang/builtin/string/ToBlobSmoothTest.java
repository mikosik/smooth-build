package org.smoothbuild.lang.builtin.string;

import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ToBlobSmoothTest extends IntegrationTestCase {

  @Test
  public void test() throws IOException {
    String content = "file content";

    script("run : toBlob('" + content + "');");
    build("run");

    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContains(artifactPath, content);
  }
}
