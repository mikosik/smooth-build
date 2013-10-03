package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.testing.type.impl.TestFileSet;

public class ZipSmoothTest extends IntegrationTestCase {

  @Test
  public void testZipping() throws IOException {
    // given
    TestFileSet fileSet = fileSet(path("dir"));
    fileSet.createFile(path("dir/fileA.txt")).createContentWithFilePath();
    fileSet.createFile(path("dir/fileB.txt")).createContentWithFilePath();
    Path outDir = path("out");
    Path outputPath = path("myOutput.zip");
    script("run : files('dir') | zip(" + outputPath + ") | save(" + outDir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();

    TestFileSet unpackedFiles = new TestFileSet();
    byte[] buffer = new byte[2048];
    int fileCount = 0;
    TestFile outputFile = fileSet(outDir).file(outputPath);
    try (ZipInputStream zipInputStream = new ZipInputStream(outputFile.openInputStream());) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        fileCount++;
        TestFile file = unpackedFiles.createFile(path(entry.getName()));
        try (OutputStream outputStream = file.openOutputStream()) {
          int len = 0;
          while ((len = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
        }
        file.assertContentContainsFilePath();
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }
}
