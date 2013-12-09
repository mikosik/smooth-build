package org.smoothbuild.lang.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ZipSmoothTest extends IntegrationTestCase {

  @Test
  public void testZipping() throws IOException {
    // given
    fileSystem.createFile(path("dir/fileA.txt"), "fileA.txt");
    fileSystem.createFile(path("dir/fileB.txt"), "fileB.txt");
    script("run : files('dir') | zip ;");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    Path artifactPath = RESULTS_PATH.append(path("run"));
    InputStream inputStream = fileSystem.openInputStream(artifactPath);
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
          assertThat(new String(outputStream.toByteArray(), CHARSET)).isEqualTo(path.value());
        }
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }
}
