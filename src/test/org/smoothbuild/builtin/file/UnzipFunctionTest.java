package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.lang.function.Param;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.lang.type.FileRo;
import org.smoothbuild.lang.type.FilesRo;
import org.smoothbuild.testing.TestingFileRw;
import org.smoothbuild.testing.TestingFilesRw;

public class UnzipFunctionTest {
  UnzipFunction unzipFunction = new UnzipFunction(new TestingFilesRw());
  @SuppressWarnings("unchecked")
  Param<FileRo> filesParam = (Param<FileRo>) unzipFunction.params().param("file");

  @Test
  public void testUnzipping() throws IOException, FunctionException {
    filesParam.set(packedFiles("file/path/file1.txt", "file/path/file2.txt"));
    FilesRo result = unzipFunction.execute();
    int fileCount = 0;
    for (FileRo file : result.asIterable()) {
      fileCount++;
      assertFileContent(file.createInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  private static TestingFileRw packedFiles(String path1, String path2) throws IOException {
    TestingFilesRw filesToPack = new TestingFilesRw();
    filesToPack.createFileRw(path(path1)).createTestContent();
    filesToPack.createFileRw(path(path2)).createTestContent();

    TestingFileRw inputFile = new TestingFilesRw().createFileRw(path("input.zip"));

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(inputFile.createOutputStream());) {
      for (FileRo fileRo : filesToPack.asIterable()) {
        addEntry(zipOutputStream, fileRo);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return inputFile;
  }

  private static void addEntry(ZipOutputStream zipOutputStream, FileRo fileRo) throws IOException {
    byte[] buffer = new byte[1024];

    ZipEntry entry = new ZipEntry(fileRo.path().value());
    zipOutputStream.putNextEntry(entry);

    try (InputStream inputStream = fileRo.createInputStream();) {
      int readCount = inputStream.read(buffer);
      while (readCount > 0) {
        zipOutputStream.write(buffer, 0, readCount);
        readCount = inputStream.read(buffer);
      }
    }

    zipOutputStream.closeEntry();
  }
}
