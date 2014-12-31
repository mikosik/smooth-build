package org.smoothbuild.builtin.file;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;
import static org.testory.Testory.then;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class FileArrayTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  Path path1 = path("file/path/file1.txt");
  Path path2 = path("file/path/file2.txt");
  private final String content1 = "content 1";
  private final String content2 = "content 2";

  @Test
  public void array_containing_two_files_is_allowed() throws Exception {
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    script(fileSystem, "result: [file(" + path1 + ") , file(" + path2 + ")];");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result").append(path1), content1);
    fileSystem.assertFileContains(artifactPath("result").append(path2), content2);
  }

  @Test
  public void array_containing_file_and_string_is_forbidden() throws Exception {
    fileSystem.createFile(path1, content1);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myFile: file(" + path1 + ") ;");
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("result: [ myFile, myString ] ;");
    script(fileSystem, scriptBuilder.build());
    buildWorker.run(asList("result"));

    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  @Test
  public void array_containing_file_and_blob_is_allowed() throws Exception {
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myFile: file(" + path1 + ") ;");
    scriptBuilder.addLine("myBlob: file(" + path2 + ") | content ;");
    scriptBuilder.addLine("result: [ myFile, myBlob] ;");
    script(fileSystem, scriptBuilder.build());
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result").append(path("0")), content1);
    fileSystem.assertFileContains(artifactPath("result").append(path("1")), content2);
  }

  @Test
  public void file_array_with_trailing_comma_is_allowed() throws Exception {
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script(fileSystem, "result: [ file(" + path1 + "), file(" + path2 + "), ];");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContainsItsPath(artifactPath("result"), path1);
    fileSystem.assertFileContainsItsPath(artifactPath("result"), path2);
  }

  @Test
  public void file_array_with_only_comma_is_forbidden() throws Exception {
    script(fileSystem, "result: [ , ];");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(SyntaxError.class);
  }

  @Test
  public void save_file_array_with_two_files() throws IOException {
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script(fileSystem, "result: [ file(" + path1 + "), file(" + path2 + ") ];");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContainsItsPath(artifactPath("result"), path1);
    fileSystem.assertFileContainsItsPath(artifactPath("result"), path2);
  }

  @Test
  public void save_file_array_with_one_file() throws IOException {
    fileSystem.createFileContainingItsPath(path1);
    script(fileSystem, "result: [ file(" + path1 + ") ];");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContainsItsPath(artifactPath("result"), path1);
  }

  @Test
  public void save_empty_file_array() throws IOException {
    script(fileSystem, "result: [ ];");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void file_array_can_contain_duplicate_values() throws Exception {
    fileSystem.createFileContainingItsPath(path1);
    script(fileSystem, "result: [ file(" + path1 + "), file(" + path1
        + ") ] | filter('nothing');\n");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void empty_file_array_can_be_saved() throws IOException {
    Path path = path("some/dir");
    fileSystem.createDir(path);
    script(fileSystem, "result: files(" + path + ");");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    then(fileSystem.filesFrom(artifactPath("result")), empty());
  }
}
