package org.smoothbuild.builtin.blob;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class BlobArrayTest {
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

  @Test
  public void array_containing_two_blobs_is_allowed() throws Exception {
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    script(fileSystem, "result: [content(file(" + path1 + ")) , content(file(" + path2 + "))];");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result").append(path("0")), content1);
    fileSystem.assertFileContains(artifactPath("result").append(path("1")), content2);
  }

  @Test
  public void array_containing_blob_and_string_is_forbidden() throws Exception {
    fileSystem.createFile(path1, content1);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myBlob: file(" + path1 + ") | content ;");
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("result: [ myBlob, myString ] ;");

    script(fileSystem, scriptBuilder.build());
    buildWorker.run(asList("result"));

    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  @Test
  public void array_containing_blob_and_file_is_allowed() throws Exception {
    fileSystem.createFile(path1, content1);
    fileSystem.createFile(path2, content2);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myBlob: file(" + path1 + ") | content ;");
    scriptBuilder.addLine("myFile: file(" + path2 + ") ;");
    scriptBuilder.addLine("result: [ myBlob, myFile] ;");

    script(fileSystem, scriptBuilder.build());
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result").append(path("0")), content1);
    fileSystem.assertFileContains(artifactPath("result").append(path("1")), content2);
  }
}
