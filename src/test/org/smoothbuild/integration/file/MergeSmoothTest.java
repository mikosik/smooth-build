package org.smoothbuild.integration.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.DuplicateMergedPathError;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.parse.ScriptBuilder;
import org.smoothbuild.testing.plugin.internal.TestFile;

public class MergeSmoothTest extends IntegrationTestCase {

  @Test
  public void mergingTwoSets() throws Exception {
    // given
    Path outputPath = path("output");
    TestFile fileA = file(path("def/fileA.txt"));
    TestFile fileB = file(path("def/fileB.txt"));
    fileA.createContentWithFilePath();
    fileB.createContentWithFilePath();

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : [ file(" + fileA.path() + ") ];");
    builder.addLine("b : [ file(" + fileB.path() + ") ];");
    builder.addLine("run : merge(files=a, with=b) | save(" + outputPath + ");");
    script(builder.build());

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    fileSet(outputPath).file(fileA.path()).assertContentContainsFilePath();
    fileSet(outputPath).file(fileB.path()).assertContentContainsFilePath();
    assertThat(fileSet(outputPath)).hasSize(2);
  }

  @Test
  public void mergingFailsWhenSetsContainFileWithTheSamePath() throws Exception {
    // given
    Path outputPath = path("output");
    Path dirAPath = path("dirA");
    Path dirBPath = path("dirB");
    Path filePath = path("def/fileA.txt");

    TestFile fileA = fileSet(dirAPath).file(filePath);
    TestFile fileB = fileSet(dirBPath).file(filePath);
    fileA.createContentWithFilePath();
    fileB.createContentWithFilePath();

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : files(" + dirAPath + ") ;");
    builder.addLine("b : files(" + dirBPath + ") ;");
    builder.addLine("run : merge(files=a, with=b) | save(" + outputPath + ");");
    script(builder.build());

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(DuplicateMergedPathError.class);
  }
}
