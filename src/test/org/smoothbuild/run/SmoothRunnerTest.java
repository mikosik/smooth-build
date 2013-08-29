package org.smoothbuild.run;

import static com.google.inject.Guice.createInjector;
import static org.smoothbuild.command.CommandLineParser.DEFAULT_SCRIPT_PATH;
import static org.smoothbuild.testing.ScriptBuilder.script;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.testing.ScriptBuilder;
import org.smoothbuild.testing.TestingFileSystem;
import org.smoothbuild.testing.TestingFileSystemModule;
import org.smoothbuild.testing.problem.TestingProblemsListener;
import org.smoothbuild.testing.problem.TestingProblemsListenerModule;

import com.google.inject.Injector;

public class SmoothRunnerTest {
  String file = "file/path/file.txt";
  String destinationDir = "destinationDir";

  TestingFileSystem fileSystem;
  SmoothRunner smoothRunner;
  TestingProblemsListener problems;

  @Before
  public void before() {
    Injector injector = createInjector(new TestingFileSystemModule(),
        new TestingProblemsListenerModule());
    fileSystem = injector.getInstance(TestingFileSystem.class);
    problems = injector.getInstance(TestingProblemsListener.class);
    smoothRunner = injector.getInstance(SmoothRunner.class);
  }

  @Test
  public void singleFileReadAndWritten_pipe() throws IOException {
    String script = script("run : file(path='" + file + "') | saveTo(dir='destinationDir');");
    fileSystem.createFileWithContent(DEFAULT_SCRIPT_PATH, script);
    fileSystem.createFileContainingPath(".", file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }

  @Test
  public void singleFileReadAndWritten_nestedCalls() throws IOException {
    String script = script("run : saveTo(dir='destinationDir', file=file(path='" + file + "') );");
    fileSystem.createFileWithContent(DEFAULT_SCRIPT_PATH, script);
    fileSystem.createFileContainingPath(".", file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }

  @Test
  public void singleFileReadAndWritten_separeteFunctions() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("filename : '" + file + "';");
    builder.addLine("myfile : file(path=filename);");
    builder.addLine("run : saveTo(file=myfile, dir='destinationDir');");
    fileSystem.createFileWithContent(DEFAULT_SCRIPT_PATH, builder.build());
    fileSystem.createFileContainingPath(".", file);

    smoothRunner.run("run");

    problems.assertNoProblems();
    fileSystem.assertFileContainsItsPath(destinationDir, file);
  }
}
