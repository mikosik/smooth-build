package org.smoothbuild.builtin.java;

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
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.util.Streams;

public class UnjarSmoothTest {
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

  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  @Test
  public void testUnjaring() throws Exception {
    // given
    Path path1 = path("a/fileA.txt");
    Path path2 = path("b/fileB.txt");
    Path jarPath = path("jar/input.jar");

    fileSystem.createDir(path1);
    fileSystem.createDir(path2);

    SFile file1 = objectsDb.file(path1);
    SFile file2 = objectsDb.file(path2);

    Blob jarBlob = JarTester.jar(file1, file2);
    Streams.copy(jarBlob.openInputStream(), fileSystem.openOutputStream(jarPath));

    script(fileSystem, "run : file(" + jarPath + ") | unjar ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();

    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }
}
