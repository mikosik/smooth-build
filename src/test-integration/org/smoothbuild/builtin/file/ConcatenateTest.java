package org.smoothbuild.builtin.file;

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
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class ConcatenateTest {
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

  @Test
  public void concatenate_files_function() throws Exception {
    // given
    Path path1 = path("def/fileA.txt");
    Path path2 = path("def/fileB.txt");
    fileSystem.createFileContainingItsPath(path1);
    fileSystem.createFileContainingItsPath(path2);

    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("a : [ file(" + path1 + ") ];");
    builder.addLine("b : [ file(" + path2 + ") ];");
    builder.addLine("run : concatenateFiles(files=a, with=b) ;");
    script(fileSystem, builder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }

}
