package org.smoothbuild.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.junit.Test;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

public class JarSmoothTest extends IntegrationTestCase {
  @Test
  public void testJaring() throws Exception {
    // given
    TestFileSet fileSet = fileSet(path("dir"));
    fileSet.createFile(path("dir/fileA.txt")).createContentWithFilePath();
    fileSet.createFile(path("dir/fileB.txt")).createContentWithFilePath();
    Path outDir = path("out");
    Path outputPath = path("myOutput.jar");
    script("run : files('dir') | jar(" + outputPath + ") | save(" + outDir + ");");

    // when
    smoothRunner.run("run");

    // then
    messages.assertNoProblems();

    TestFileSet unpackedFiles = new TestFileSet();
    byte[] buffer = new byte[2048];
    int fileCount = 0;
    TestFile outputFile = fileSet(outDir).file(outputPath);
    try (JarInputStream jarInputStream = new JarInputStream(outputFile.openInputStream());) {
      JarEntry entry = null;
      while ((entry = jarInputStream.getNextJarEntry()) != null) {
        fileCount++;
        TestFile file = unpackedFiles.createFile(path(entry.getName()));
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
