package org.smoothbuild.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.SmoothContants.CHARSET;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class JarSmoothTest extends IntegrationTestCase {
  @Test
  public void testJaring() throws Exception {
    // given
    Path root = path("dir");
    Path path1 = path("dir/fileA.txt");
    Path path2 = path("dir/fileB.txt");
    fileSystem.createFileContainingItsPath(root, path1);
    fileSystem.createFileContainingItsPath(root, path2);

    script("run : files(" + root + ") | jar ;");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    Path artifactPath = RESULTS_PATH.append(path("run"));
    InputStream inputStream = fileSystem.openInputStream(artifactPath);
    try (JarInputStream jarInputStream = new JarInputStream(inputStream);) {
      JarEntry entry = null;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        fileCount++;
        Path path = path(entry.getName());
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
          int len = 0;
          while ((len = jarInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
          assertThat(new String(outputStream.toByteArray(), CHARSET)).isEqualTo(path.value());
        }
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }
}
