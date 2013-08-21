package org.smoothbuild.builtin.file;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingFileContent.assertFileContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.smoothbuild.builtin.file.UnzipFunction.Parameters;
import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Files;
import org.smoothbuild.plugin.exc.FunctionException;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFiles;

public class UnzipFunctionTest {
  UnzipFunction unzipFunction = new UnzipFunction(new TestingFiles());

  @Test
  public void testUnzipping() throws IOException, FunctionException {
    Parameters params = params(packedFiles("file/path/file1.txt", "file/path/file2.txt"));
    Files result = unzipFunction.execute(params);
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

  private static UnzipFunction.Parameters params(final File file) {
    return new UnzipFunction.Parameters() {
      @Override
      public File file() {
        return file;
      }
    };
  }
}
