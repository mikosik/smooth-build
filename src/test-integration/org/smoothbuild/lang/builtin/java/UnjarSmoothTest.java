package org.smoothbuild.lang.builtin.java;

import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class UnjarSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnjaring() throws Exception {
    // given
    Path path1 = path("a/fileA.txt");
    Path path2 = path("b/fileB.txt");
    File jarFile = JarTester.jaredFiles(fileSystem, path1.value(), path2.value());

    script("run : file(" + jarFile.path() + ") | unjar ;");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();

    Path artifactPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(artifactPath, path1);
    fileSystem.assertFileContainsItsPath(artifactPath, path2);
  }
}
