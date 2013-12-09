package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class ConcatenateFilesSmoothTest extends IntegrationTestCase {
  @Test
  public void concatenating_two_file_arrays() throws Exception {
    // given
    Path path1 = path("def/fileA.txt");
    Path path2 = path("def/fileB.txt");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : [ file(" + path1 + ") ];");
    builder.addLine("b : [ file(" + path2 + ") ];");
    builder.addLine("run : concatenateFiles(files=a, with=b) ;");
    script(builder.build());

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

}
