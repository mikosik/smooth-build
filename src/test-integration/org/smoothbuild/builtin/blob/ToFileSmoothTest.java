package org.smoothbuild.builtin.blob;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
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
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ToFileSmoothTest {
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
  public void test() throws IOException {
    String content = "file content";
    Path sourcePath = path("source/path/file.txt");
    Path destinationPath = path("destination/path/file.txt");

    fileSystem.createFile(sourcePath, content);

    script(fileSystem,
        "run : [ toFile(path=" + destinationPath + ", content=file(" + sourcePath + ")) ];");

    buildWorker.run(asList("run"));

    userConsole.messages().assertNoProblems();
    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContains(artifactPath.append(destinationPath), content);
  }
}
