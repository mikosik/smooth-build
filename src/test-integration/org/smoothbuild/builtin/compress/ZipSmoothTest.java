package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

import com.google.common.base.Charsets;

public class ZipSmoothTest extends IntegrationTestCase {

  @Test
  public void testZipping() throws IOException {
    // given
    fileSystem.createFile(path("dir/fileA.txt"), "fileA.txt");
    fileSystem.createFile(path("dir/fileB.txt"), "fileB.txt");
    Path outDir = path("out");
    Path outputPath = path("myOutput.zip");
    script("run : files('dir') | zip(" + outputPath + ") | save(" + outDir + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    InputStream inputStream = fileSystem.openInputStream(outDir.append(outputPath));
    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream);) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        fileCount++;

        Path path = path(entry.getName());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
          int len = 0;
          while ((len = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
          assertThat(new String(outputStream.toByteArray(), Charsets.UTF_8))
              .isEqualTo(path.value());
        }
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }
}
