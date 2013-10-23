package org.smoothbuild.builtin.compress;

import static org.smoothbuild.fs.base.Path.path;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.common.ZipTester;
import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.testing.type.impl.FakeFileSet;
import org.smoothbuild.testing.type.impl.FileTester;

public class UnzipSmoothTest extends IntegrationTestCase {

  @Test
  public void testUnzipping() throws Exception {
    // given
    Path fileA = path("a/fileA.txt");
    Path fileB = path("b/fileB.txt");
    FakeFile zipFile = ZipTester.zippedFiles(fileSystem, fileA.value(), fileB.value());

    script("run : file(" + zipFile.path() + ") | unzip | save('out');");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();

    FakeFileSet outFiles = fileSet(path("out"));
    outFiles.contains(fileA);
    outFiles.contains(fileB);
    FileTester.assertContentContainsFilePath(outFiles.file(fileA));
  }
}
