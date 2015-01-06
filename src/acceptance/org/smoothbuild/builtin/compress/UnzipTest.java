package org.smoothbuild.builtin.compress;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.artifactPath;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.common.ZipTester;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class UnzipTest {
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
  public void unzip_function() throws Exception {
    Path path1 = path("a/fileA.txt");
    Path path2 = path("b/fileB.txt");
    Path zipFile = ZipTester.zippedFiles(fileSystem, path1.value(), path2.value());

    script(fileSystem, "result : file(" + zipFile + ") | unzip ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContainsItsPath(artifactPath("result"), path1);
    fileSystem.assertFileContainsItsPath(artifactPath("result"), path2);
  }
}
