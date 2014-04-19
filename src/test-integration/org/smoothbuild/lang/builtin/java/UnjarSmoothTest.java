package org.smoothbuild.lang.builtin.java;

import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.util.Streams;

public class UnjarSmoothTest extends IntegrationTestCase {
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

    SBlob jarBlob = JarTester.jar(file1, file2);
    Streams.copy(jarBlob.openInputStream(), fileSystem.openOutputStream(jarPath));

    script("run : file(" + jarPath + ") | unjar ;");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();

    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }
}
