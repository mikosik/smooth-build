package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Test;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.FilesRo;
import org.smoothbuild.testing.TestingFileRw;
import org.smoothbuild.testing.TestingFilesRw;

public class ZipFunctionTest {
  // TODO pass as argument object that throws exception when second file is
  // created. Or maybe even method that instantiate function given
  // ZipFunction.class

  ZipFunction zipFunction = new ZipFunction(new TestingFilesRw());
  @SuppressWarnings("unchecked")
  Param<FilesRo> filesParam = (Param<FilesRo>) zipFunction.params().param("files");

  @Test
  public void testZipping() throws IOException, FunctionException {
    TestingFilesRw inputFiles = new TestingFilesRw();
    inputFiles.createFileRw(path("fileA.txt")).createTestContent();
    inputFiles.createFileRw(path("fileB.txt")).createTestContent();

    filesParam.set(inputFiles);

    FileRo result = zipFunction.execute();

    TestingFilesRw unpackedFiles = new TestingFilesRw();

    byte[] buffer = new byte[2048];
    int fileCount = 0;
    try (ZipInputStream zipInputStream = new ZipInputStream(result.createInputStream());) {
      ZipEntry entry = null;
      while ((entry = zipInputStream.getNextEntry()) != null) {
        fileCount++;
        TestingFileRw file = unpackedFiles.createFileRw(path(entry.getName()));
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
}
