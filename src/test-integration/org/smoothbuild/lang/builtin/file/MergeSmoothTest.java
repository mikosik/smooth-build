package org.smoothbuild.lang.builtin.file;

import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class MergeSmoothTest extends IntegrationTestCase {

  @Test
  public void mergingTwoArrays() throws Exception {
    // given
    Path path1 = path("def/fileA.txt");
    Path path2 = path("def/fileB.txt");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : [ file(" + path1 + ") ];");
    builder.addLine("b : [ file(" + path2 + ") ];");
    builder.addLine("run : merge(files=a, with=b) ;");
    script(builder.build());

    // when
    build("run");

    // then
    userConsole.assertNoProblems();
    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void mergingFailsWhenArraysContainFileWithTheSamePath() throws Exception {
    // given
    Path dir1Path = path("dirA");
    Path dir2Path = path("dirB");
    Path filePath = path("def/fileA.txt");

    fileSystem.createFileContainingItsPath(dir1Path, filePath);
    fileSystem.createFileContainingItsPath(dir2Path, filePath);

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : files(" + dir1Path + ") ;");
    builder.addLine("b : files(" + dir2Path + ") ;");
    builder.addLine("run : merge(files=a, with=b) ;");
    script(builder.build());

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(DuplicateMergedPathError.class);
  }
}
