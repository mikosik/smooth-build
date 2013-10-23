package org.smoothbuild.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.testing.type.impl.FakeFileSet;

public class JarSmoothTest extends IntegrationTestCase {
  @Test
  public void testJaring() throws Exception {
    // given
    Path root = path("dir");
    Path path1 = path("dir/fileA.txt");
    Path path2 = path("dir/fileB.txt");
    fileSystem.createFileContainingItsPath(root, path1);
    fileSystem.createFileContainingItsPath(root, path2);

    Path outDir = path("out");
    Path outputPath = path("myOutput.jar");
    script("run : files(" + root + ") | jar(" + outputPath + ") | save(" + outDir + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();

    FakeFileSet unpackedFiles = new FakeFileSet();
    byte[] buffer = new byte[2048];
    int fileCount = 0;
    InputStream inputStream = fileSystem.openInputStream(outDir.append(outputPath));
    try (JarInputStream jarInputStream = new JarInputStream(inputStream);) {
      JarEntry entry = null;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        fileCount++;
        FakeFile file = unpackedFiles.createFile(path(entry.getName()));
        try (OutputStream outputStream = file.openOutputStream()) {
          int len = 0;
          while ((len = jarInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
        }
        file.assertContentContainsFilePath();
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }
}
