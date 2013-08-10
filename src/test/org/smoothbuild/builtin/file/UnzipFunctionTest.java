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
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Files;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFiles;

public class UnzipFunctionTest {
  UnzipFunction unzipFunction = new UnzipFunction(new TestingFiles());
  @SuppressWarnings("unchecked")
  Param<File> filesParam = (Param<File>) unzipFunction.params().param("file");

  @Test
  public void testUnzipping() throws IOException, FunctionException {
    filesParam.set(packedFiles("file/path/file1.txt", "file/path/file2.txt"));
    Files result = unzipFunction.execute();
    int fileCount = 0;
    for (File file : result.asIterable()) {
      fileCount++;
      assertFileContent(file.createInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  private static TestingFile packedFiles(String path1, String path2) throws IOException {
    TestingFiles filesToPack = new TestingFiles();
    filesToPack.createFile(path(path1)).createTestContent();
    filesToPack.createFile(path(path2)).createTestContent();

    TestingFile inputFile = new TestingFiles().createFile(path("input.zip"));

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(inputFile.createOutputStream());) {
      for (File file : filesToPack.asIterable()) {
        addEntry(zipOutputStream, file);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return inputFile;
  }

  private static void addEntry(ZipOutputStream zipOutputStream, File file) throws IOException {
    byte[] buffer = new byte[1024];

    ZipEntry entry = new ZipEntry(file.path().value());
    zipOutputStream.putNextEntry(entry);

    try (InputStream inputStream = file.createInputStream();) {
      int readCount = inputStream.read(buffer);
      while (readCount > 0) {
        zipOutputStream.write(buffer, 0, readCount);
        readCount = inputStream.read(buffer);
      }
    }

    zipOutputStream.closeEntry();
  }
}
