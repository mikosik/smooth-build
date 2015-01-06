package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.artifactPath;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;
import static org.testory.Testory.then;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ImplicitConversionTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new AcceptanceTestModule()).injectMembers(this);
  }

  @Test
  public void file_is_implicitly_converted_to_blob() throws IOException {
    Path path = path("file.txt");
    fileSystem.createFile(path, "abc");
    script(fileSystem, "result: file(" + path + ") | toString ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void file_array_is_implicitly_converted_to_blob_array() throws IOException {
    fileSystem.createFile(path("file1.txt"), "abc");
    fileSystem.createFile(path("file2.txt"), "def");
    script(fileSystem, "result: concatenateBlobs([file('file1.txt')], with=[file('file2.txt')]) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result").append(path("0")), "abc");
    fileSystem.assertFileContains(artifactPath("result").append(path("1")), "def");
  }

  @Test
  public void nil_is_implicitly_converted_to_string_array() throws IOException {
    script(fileSystem, "result: concatenateStrings([], with=[]) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    then(fileSystem.filesFrom(artifactPath("result")), empty());
  }

  @Test
  public void nil_is_implicitly_converted_to_blob_array() throws IOException {
    script(fileSystem, "result: concatenateBlobs([], with=[]) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    then(fileSystem.filesFrom(artifactPath("result")), empty());
  }

  @Test
  public void nil_is_implicitly_converted_to_file_array() throws IOException {
    script(fileSystem, "result: concatenateFiles([], with=[]) ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    then(fileSystem.filesFrom(artifactPath("result")), empty());
  }
}
