package org.smoothbuild.lang.builtin.blob;

import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class ConcatenateBlobsSmoothTest extends IntegrationTestCase {
  @Test
  public void concatenating_two_blob_arrays() throws Exception {
    // given
    Path path1 = path("def/fileA.txt");
    Path path2 = path("def/fileB.txt");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : [ file(" + path1 + ") ];");
    builder.addLine("b : [ file(" + path2 + ") ];");
    builder.addLine("run : concatenateBlobs(blobs=a, with=b) ;");
    script(builder.build());

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContains(artifactPath.append(path("0")), path1.value());
    fileSystem.assertFileContains(artifactPath.append(path("1")), path2.value());
  }
}
