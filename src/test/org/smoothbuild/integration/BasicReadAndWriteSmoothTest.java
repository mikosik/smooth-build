package org.smoothbuild.integration;

import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.ScriptBuilder;

public class BasicReadAndWriteSmoothTest extends IntegrationTestCase {
  Path file = path("file/path/file.txt");
  Path destinationDir = path("destination/dir");

  @Test
  public void singleFileReadAndWritten_pipe() throws IOException {
    script("run : file(path='" + file.value() + "') | save(dir='" + destinationDir.value() + "');");
    fileSystem.createFileContainingItsPath(file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }

  @Test
  public void singleFileReadAndWritten_nestedCalls() throws IOException {
    script("run : save(dir='" + destinationDir.value() + "', file=file(path='" + file.value()
        + "') );");
    fileSystem.createFileContainingItsPath(file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }

  @Test
  public void singleFileReadAndWritten_separeteFunctions() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("filename : '" + file.value() + "';");
    builder.addLine("myfile : file(path=filename);");
    builder.addLine("run : save(file=myfile, dir='" + destinationDir.value() + "');");
    script(builder.build());
    fileSystem.createFileContainingItsPath(file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }
}
