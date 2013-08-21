package org.smoothbuild.builtin.compress;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.plugin.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Files;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFiles;

public class ZipFunctionTest {
  // TODO pass as argument object that throws exception when second file is
  // created. Or maybe even method that instantiate function given
  // ZipFunction.class

  ZipFunction zipFunction = new ZipFunction(new TestingFiles());

  @Test
  public void testZipping() throws IOException, FunctionException {
    TestingFiles inputFiles = new TestingFiles();
    inputFiles.createFile(path("fileA.txt")).createTestContent();
    inputFiles.createFile(path("fileB.txt")).createTestContent();

    File result = zipFunction.execute(params(inputFiles));

    TestingFiles unpackedFiles = new TestingFiles();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    try (ZipInputStream zipInputStream = new ZipInputStream(result.createInputStream());) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        fileCount++;
        TestingFile file = unpackedFiles.createFile(path(entry.getName()));
        try (OutputStream outputStream = file.createOutputStream()) {
          int len = 0;
          while ((len = zipInputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, len);
          }
        }
        file.assertTestContent();
      }
    }
    assertThat(fileCount).isEqualTo(2);
  }

  private static ZipFunction.Parameters params(final Files files) {
    return new ZipFunction.Parameters() {
      @Override
      public Files files() {
        return files;
      }
    };
  }
}
