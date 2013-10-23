package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.testing.type.impl.FakeFileSet;

public class ZipSmoothTest extends IntegrationTestCase {

  @Test
  public void testZipping() throws IOException {
    // given
    fileSystem.createFileWithContent(path("dir/fileA.txt"), "fileA.txt");
    fileSystem.createFileWithContent(path("dir/fileB.txt"), "fileB.txt");
    Path outDir = path("out");
    Path outputPath = path("myOutput.zip");
    script("run : files('dir') | zip(" + outputPath + ") | save(" + outDir + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();

    FakeFileSet unpackedFiles = new FakeFileSet();
    byte[] buffer = new byte[2048];
    int fileCount = 0;
    InputStream inputStream = fileSystem.openInputStream(outDir.append(outputPath));
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream);) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        fileCount++;
        FakeFile file = unpackedFiles.createFile(path(entry.getName()));
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
