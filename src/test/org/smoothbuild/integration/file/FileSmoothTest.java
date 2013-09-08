package org.smoothbuild.integration.file;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.ScriptBuilder;

public class FileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile_pipe() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    Path dir = path("destination/dir");
    script("run : file(path='" + file.value() + "') | save(dir='" + dir.value() + "');");
    fileSystem.createFileContainingItsPath(file);

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
  }

  @Test
  public void saveFile_nestedCalls() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    Path dir = path("destination/dir");
    script("run : save(dir='" + dir.value() + "', file=file(path='" + file.value() + "') );");
    fileSystem.createFileContainingItsPath(file);

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
  }

  @Test
  public void saveFile_separeteFunctions() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    Path dir = path("destination/dir");
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("filename : '" + file.value() + "';");
    builder.addLine("myfile : file(path=filename);");
    builder.addLine("run : save(file=myfile, dir='" + dir.value() + "');");
    script(builder.build());
    fileSystem.createFileContainingItsPath(file);

    // when
    smoothRunner.run("run");

    // then
    problems.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
  }
}
