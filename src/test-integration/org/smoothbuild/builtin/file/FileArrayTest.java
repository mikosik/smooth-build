package org.smoothbuild.builtin.file;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;
import static org.testory.Testory.*;

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

    script(fileSystem, "run: [file(" + path1 + ") , file(" + path2 + ")];");
    buildWorker.run(asList("run"));

    userConsole.messages().assertNoProblems();
    Path arrayArtifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContains(arrayArtifactPath.append(path1), content1);
    fileSystem.assertFileContains(arrayArtifactPath.append(path2), content2);
  }

  @Test
  public void array_containing_file_and_string_is_forbidden() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myFile: file(" + path1 + ") ;");
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("run: [ myFile, myString ] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  @Test
  public void array_containing_file_and_blob_is_allowed() throws Exception {
    // given
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myFile: file(" + path1 + ") ;");
    scriptBuilder.addLine("myBlob: file(" + path2 + ") | content ;");
    scriptBuilder.addLine("run: [ myFile, myBlob] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then

    userConsole.messages().assertNoProblems();
    Path arrayArtifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContains(arrayArtifactPath.append(path("0")), content1);
    fileSystem.assertFileContains(arrayArtifactPath.append(path("1")), content2);
  }

  @Test
  public void file_array_with_trailing_comma_is_allowed() throws Exception {
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script(fileSystem, "run : [ file(" + path1 + "), file(" + path2 + "), ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void file_array_with_only_comma_is_forbidden() throws Exception {
    // given
    script(fileSystem, "run : [ , ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(SyntaxError.class);
  }

  @Test
  public void save_file_array_with_two_files() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    script(fileSystem, "run : [ file(" + path1 + "), file(" + path2 + ") ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

  @Test
  public void save_file_array_with_one_file() throws IOException {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script(fileSystem, "run : [ file(" + path1 + ") ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
  }

  @Test
  public void save_empty_file_array() throws IOException {
    // given
    script(fileSystem, "run : [ ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void file_array_can_contain_duplicate_values() throws Exception {
    // given
    fileSystem.createFileContainingItsPath(path1);

    script(fileSystem, "run : [ file(" + path1 + "), file(" + path1 + ") ] | filter('nothing');\n");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void empty_file_array_can_be_saved() throws IOException {
    // given
    Path path = path("some/dir");
    fileSystem.createDir(path);
    script(fileSystem, "run : files(" + path + ");");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();

    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    then(fileSystem.filesFrom(artifactPath), empty());
  }
}
