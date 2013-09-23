package org.smoothbuild.integration.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

public class ZipSmoothTest extends IntegrationTestCase {

  @Test
  public void testZipping() throws IOException {
    // given
    TestFileSystem files = fileSystem.subFileSystem(path("dir"));
    files.createFileContainingItsPath(path("dir/fileA.txt"));
    files.createFileContainingItsPath(path("dir/fileB.txt"));
    script("run : files('dir') | zip | save('out');");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();

    TestFileSet unpackedFiles = new TestFileSet();
    byte[] buffer = new byte[2048];
    int fileCount = 0;
    Path outputPath = path("out/output.zip");
    try (ZipInputStream zipInputStream = new ZipInputStream(fileSystem.openInputStream(outputPath));) {
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
