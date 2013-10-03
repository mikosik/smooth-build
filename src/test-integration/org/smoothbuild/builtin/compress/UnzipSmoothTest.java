package org.smoothbuild.builtin.compress;

import static org.smoothbuild.type.api.Path.path;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.common.ZipTester;
import org.smoothbuild.testing.type.impl.FileTester;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.testing.type.impl.TestFileSet;
import org.smoothbuild.type.api.Path;

public class UnzipSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnzipping() throws Exception {
    // given
    Path fileA = path("a/fileA.txt");
    Path fileB = path("b/fileB.txt");
    TestFile zipFile = file(path("input.zip"));
    ZipTester.zipFiles(zipFile, fileA.value(), fileB.value());

    script("run : file(" + zipFile.path() + ") | unzip | save('out');");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();

    TestFileSet outFiles = fileSet(path("out"));
    outFiles.contains(fileA);
    outFiles.contains(fileB);
    FileTester.assertContentContainsFilePath(outFiles.file(fileA));
  }
}
