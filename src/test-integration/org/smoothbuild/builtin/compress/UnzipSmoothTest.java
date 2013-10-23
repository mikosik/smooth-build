package org.smoothbuild.builtin.compress;

import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.common.ZipTester;

public class UnzipSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnzipping() throws Exception {
    // given
    Path outPath = path("out");
    Path fileA = path("a/fileA.txt");
    Path fileB = path("b/fileB.txt");
    Path zipFile = ZipTester.zippedFiles(fileSystem, fileA.value(), fileB.value());

    script("run : file(" + zipFile + ") | unzip | save(" + outPath + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();

    fileSystem.assertFileContainsItsPath(outPath, fileA);
    fileSystem.assertFileContainsItsPath(outPath, fileB);
  }
}
