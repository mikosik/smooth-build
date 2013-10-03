package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.task.err.DuplicatePathError;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.testing.type.impl.TestFileSet;

import com.google.common.collect.Iterables;

public class FileSetSmoothTest extends IntegrationTestCase {

  @Test
  public void setWithTrailingCommaIsAllowed() throws Exception {
    Path dir = path("destination/dir");
    TestFile file1 = file(path("file/path/file1.txt"));
    TestFile file2 = file(path("file/path/file2.txt"));
    file1.createContentWithFilePath();
    file2.createContentWithFilePath();

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + file1.path() + "), file(" + file2.path() + "), ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    TestFileSet resultFiles = fileSet(dir);
    assertThat(Iterables.size(resultFiles)).isEqualTo(2);
  }

  @Test
  public void setWithOnlyCommaIsForbidden() throws Exception {
    // given
    script("run : [ , ];");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(SyntaxError.class);
  }

  @Test
  public void saveFileSetWithTwoFiles() throws IOException {
    // given
    Path dir = path("destination/dir");
    TestFile file1 = file(path("file/path/file1.txt"));
    TestFile file2 = file(path("file/path/file2.txt"));
    file1.createContentWithFilePath();
    file2.createContentWithFilePath();

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + file1.path() + "), file(" + file2.path() + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    TestFileSet resultFiles = fileSet(dir);
    resultFiles.file(file1.path()).assertContentContainsFilePath();
    resultFiles.file(file2.path()).assertContentContainsFilePath();
    assertThat(Iterables.size(resultFiles)).isEqualTo(2);
  }

  @Test
  public void saveFileSetWithOneFile() throws IOException {
    // given
    Path dir = path("destination/dir");
    TestFile file1 = file(path("file/path/file1.txt"));
    file1.createContentWithFilePath();

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + file1.path() + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    TestFileSet resultFiles = fileSet(dir);
    resultFiles.file(file1.path()).assertContentContainsFilePath();
    assertThat(Iterables.size(resultFiles)).isEqualTo(1);
  }

  @Test
  public void saveEmptyFileSet() throws IOException {
    // given
    Path dir = path("destination/dir");

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    TestFileSet resultFiles = fileSet(dir);
    assertThat(Iterables.size(resultFiles)).isEqualTo(0);
  }

  @Test
  public void fileSetWithDuplicatedFiles() throws Exception {
    // given
    TestFile file1 = file(path("file/path/file1.txt"));
    file1.createContentWithFilePath();

    script("run : [ file(" + file1.path() + "), file(" + file1.path() + ") ];\n");

    // when
    smoothRunner.run("run");

    // then
    messages.assertOnlyProblem(DuplicatePathError.class);
  }
}
