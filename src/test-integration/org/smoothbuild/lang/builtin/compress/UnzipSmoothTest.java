package org.smoothbuild.lang.builtin.compress;

import static org.smoothbuild.io.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.common.ZipTester;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class UnzipSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnzipping() throws Exception {
    // given
    Path path1 = path("a/fileA.txt");
    Path path2 = path("b/fileB.txt");
    Path zipFile = ZipTester.zippedFiles(fileSystem, path1.value(), path2.value());

    script("run : file(" + zipFile + ") | unzip ;");

    // when
    build("run");

    // then
    userConsole.messageGroup().assertNoProblems();

    Path dirPath = RESULTS_PATH.append(path("run"));
    fileSystem.assertFileContainsItsPath(dirPath, path1);
    fileSystem.assertFileContainsItsPath(dirPath, path2);
  }
}
