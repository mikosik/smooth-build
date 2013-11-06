package org.smoothbuild.builtin.file;

import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class MergeSmoothTest extends IntegrationTestCase {

  @Test
  public void mergingTwoSets() throws Exception {
    // given
    Path outputPath = path("output");
    Path path1 = path("def/fileA.txt");
    Path path2 = path("def/fileB.txt");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : [ file(" + path1 + ") ];");
    builder.addLine("b : [ file(" + path2 + ") ];");
    builder.addLine("run : merge(files=a, with=b) | save(" + outputPath + ");");
    script(builder.build());

    // when
    smoothApp.run("run");

    // then
    userConsole.assertNoProblems();
    fileSystem.assertFileContainsItsPath(outputPath, path1);
    fileSystem.assertFileContainsItsPath(outputPath, path2);
  }

  @Test
  public void mergingFailsWhenSetsContainFileWithTheSamePath() throws Exception {
    // given
    Path dir1Path = path("dirA");
    Path dir2Path = path("dirB");
    Path filePath = path("def/fileA.txt");
    Path outputPath = path("output");

    fileSystem.createFileContainingItsPath(dir1Path, filePath);
    fileSystem.createFileContainingItsPath(dir2Path, filePath);

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : files(" + dir1Path + ") ;");
    builder.addLine("b : files(" + dir2Path + ") ;");
    builder.addLine("run : merge(files=a, with=b) | save(" + outputPath + ");");
    script(builder.build());

    // when
    smoothApp.run("run");

    // then
    userConsole.assertOnlyProblem(DuplicateMergedPathError.class);
  }
}
