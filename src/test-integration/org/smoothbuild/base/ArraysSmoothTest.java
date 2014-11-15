package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class ArraysSmoothTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  private final Path path1 = path("some/path1");
  private final Path path2 = path("some/path2");
  private final String content1 = "content 1";
  private final String content2 = "content 2";

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  // arrays with String as first element

  @Test
  public void array_containing_two_strings_is_allowed() throws Exception {
    script(fileSystem, "run: ['" + content1 + "' , '" + content2 + "'];");
    buildWorker.run(asList("run"));

    userConsole.messages().assertNoProblems();
    Path arrayArtifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContains(arrayArtifactPath.append(path("0")), content1);
    fileSystem.assertFileContains(arrayArtifactPath.append(path("1")), content2);
  }

  @Test
  public void array_containing_string_and_blob_is_forbidden() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myBlob: file(" + path1 + ") | content ;");
    scriptBuilder.addLine("run: [ myString, myBlob ] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  @Test
  public void array_containing_string_and_file_is_forbidden() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myFile: file(" + path1 + ") ;");
    scriptBuilder.addLine("run: [ myString, myFile ] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  // arrays with Blob as first element

  @Test
  public void array_containing_blob_and_string_is_forbidden() throws Exception {
    // given
    fileSystem.createFile(path1, content1);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myBlob: file(" + path1 + ") | content ;");
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("run: [ myBlob, myString ] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  @Test
  public void array_containing_two_blobs_is_allowed() throws Exception {
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    script(fileSystem, "run: [content(file(" + path1 + ")) , content(file(" + path2 + "))];");
    buildWorker.run(asList("run"));

    userConsole.messages().assertNoProblems();
    Path arrayArtifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContains(arrayArtifactPath.append(path("0")), content1);
    fileSystem.assertFileContains(arrayArtifactPath.append(path("1")), content2);
  }

  @Test
  public void array_containing_blob_and_file_is_allowed() throws Exception {
    // given
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myBlob: file(" + path1 + ") | content ;");
    scriptBuilder.addLine("myFile: file(" + path2 + ") ;");
    scriptBuilder.addLine("run: [ myBlob, myFile] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then

    userConsole.messages().assertNoProblems();
    Path arrayArtifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContains(arrayArtifactPath.append(path("0")), content1);
    fileSystem.assertFileContains(arrayArtifactPath.append(path("1")), content2);
  }

  // arrays with File as first element

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

  // nested arrays

  @Test
  public void nested_arrays_are_forbidden() throws IOException {
    // given
    script(fileSystem, "run : [ [ 'abc' ] ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(SyntaxError.class);
  }

  /**
   * arrays nested inline ( [[ 'abc' ]] ) are detected as syntax errors while
   * those passed as function results ([ myArray() ]) are detected by parser.
   * This will probably change in future when more complicated expression are
   * allowed.
   */
  @Test
  public void nested_arrays_are_forbidden_regression_test() throws IOException {
    // given
    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("someArray: [ 'abc' ] ;");
    scriptBuilder.addLine("run: [ someArray ] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(ForbiddenArrayElemError.class);
  }

  @Test
  public void nil_can_be_saved() throws IOException {
    // given
    script(fileSystem, "run : [];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();

    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    assertThat(fileSystem.pathState(artifactPath)).isEqualTo(PathState.DIR);
    assertThat(fileSystem.filesFrom(artifactPath)).isEmpty();
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
    assertThat(fileSystem.pathState(artifactPath)).isEqualTo(PathState.DIR);
    assertThat(fileSystem.filesFrom(artifactPath)).isEmpty();
  }
}
