package org.smoothbuild.builtin.string;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
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

public class StringArrayTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  private final Path path1 = path("some/path1");
  private final String content1 = "content 1";
  private final String content2 = "content 2";

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

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

}
