package org.smoothbuild.builtin.java;

import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class UnjarSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnjaring() throws Exception {
    // given
    Path path1 = path("a/fileA.txt");
    Path path2 = path("b/fileB.txt");
    Path outDir = path("out");
    File jarFile = JarTester.jaredFiles(fileSystem, path1.value(), path2.value());

    script("run : file(" + jarFile.path() + ") | unjar | save(" + outDir + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();

    fileSystem.assertFileContainsItsPath(outDir, path1);
    fileSystem.assertFileContainsItsPath(outDir, path2);
  }
}
