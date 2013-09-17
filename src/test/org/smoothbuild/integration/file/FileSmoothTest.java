package org.smoothbuild.integration.file;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class FileSmoothTest extends IntegrationTestCase {

  @Test
  public void saveFile_pipe() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    Path dir = path("destination/dir");
    script("run : file(" + file + ") | save(" + dir + ");");
    fileSystem.createFileContainingItsPath(file);

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
  }

  @Test
  public void saveFile_nestedCalls() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    Path dir = path("destination/dir");
    script("run : save(" + dir + ", file(" + file + ") );");
    fileSystem.createFileContainingItsPath(file);

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
  }

  @Test
  public void saveFile_separeteFunctions() throws IOException {
    // given
    Path file = path("file/path/file.txt");
    Path dir = path("destination/dir");
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("filename : " + file + ";");
    builder.addLine("myfile : file(filename);");
    builder.addLine("run : save(myfile, " + dir + ");");
    script(builder.build());
    fileSystem.createFileContainingItsPath(file);

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();
    fileSystem.subFileSystem(dir).assertFileContainsItsPath(file);
  }
}
