package org.smoothbuild.integration;

import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.ScriptBuilder.script;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.ScriptBuilder;

public class BasicReadAndWriteSmoothTest extends IntegrationTestCase {
  Path file = path("file/path/file.txt");
  Path destinationDir = path("destination/dir");

  @Test
  public void singleFileReadAndWritten_pipe() throws IOException {
    String script = script("run : file(path='" + file.value() + "') | saveTo(dir='"
        + destinationDir.value() + "');");
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, script);
    fileSystem.createFileContainingItsPath(file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }

  @Test
  public void singleFileReadAndWritten_nestedCalls() throws IOException {
    String script = script("run : saveTo(dir='" + destinationDir.value() + "', file=file(path='"
        + file.value() + "') );");
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, script);
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
    builder.addLine("run : saveTo(file=myfile, dir='" + destinationDir.value() + "');");
    fileSystem.createFileWithContent(DEFAULT_SCRIPT, builder.build());
    fileSystem.createFileContainingItsPath(file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }
}
