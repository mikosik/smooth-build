package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.object.err.DuplicatePathError;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.type.impl.FakeFileSet;

import com.google.common.collect.Iterables;

public class FileSetSmoothTest extends IntegrationTestCase {
  Path path1 = path("file/path/file1.txt");
  Path path2 = path("file/path/file2.txt");

  @Test
  public void setWithTrailingCommaIsAllowed() throws Exception {
    Path dir = path("destination/dir");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + path1 + "), file(" + path2 + "), ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    FakeFileSet resultFiles = fileSet(dir);
    assertThat(Iterables.size(resultFiles)).isEqualTo(2);
  }

  @Test
  public void setWithOnlyCommaIsForbidden() throws Exception {
    // given
    script("run : [ , ];");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(SyntaxError.class);
  }

  @Test
  public void saveFileSetWithTwoFiles() throws IOException {
    // given
    Path dir = path("destination/dir");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + path1 + "), file(" + path2 + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    FakeFileSet resultFiles = fileSet(dir);
    resultFiles.file(path1).assertContentContainsFilePath();
    resultFiles.file(path2).assertContentContainsFilePath();
    assertThat(Iterables.size(resultFiles)).isEqualTo(2);
  }

  @Test
  public void saveFileSetWithOneFile() throws IOException {
    // given
    Path dir = path("destination/dir");

    fileSystem.createFileContainingItsPath(path1);

    StringBuilder builder = new StringBuilder();
    builder.append("myfiles : [ file(" + path1 + ") ];\n");
    builder.append("run : myfiles | save(" + dir + ");\n");
    script(builder.toString());

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    FakeFileSet resultFiles = fileSet(dir);
    resultFiles.file(path1).assertContentContainsFilePath();
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
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    FakeFileSet resultFiles = fileSet(dir);
    assertThat(Iterables.size(resultFiles)).isEqualTo(0);
  }

  @Test
  public void fileSetWithDuplicatedFiles() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script("run : [ file(" + path1 + "), file(" + path1 + ") ];\n");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(DuplicatePathError.class);
  }
}
