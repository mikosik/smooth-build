package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.PluginErrorException;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

public class UnzipperTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";

  TestFileSet resultFileSet = new TestFileSet();
  Unzipper unzipper = new Unzipper();

  @Test
  public void unzipping() throws Exception {
    TestFile zipFile = zippedFiles(fileName1, fileName2);

    unzipper.unzipFile(zipFile, resultFileSet);

    int fileCount = 0;
    for (File file : resultFileSet) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    TestFile zipFile = zippedFiles(illegalFileName);

    try {
      unzipper.unzipFile(zipFile, resultFileSet);
      fail("exception should be thrown");
    } catch (PluginErrorException e) {
      // expected
      assertThat(e.error()).isInstanceOf(IllegalPathInZipError.class);
    }
  }

  private static TestFile zippedFiles(String... fileNames) throws IOException {
    TestFile inputFile = new TestFileSet().createFile(path("input.zip"));

    try (ZipOutputStream zipOutputStream = new ZipOutputStream(inputFile.openOutputStream());) {
      for (String fileName : fileNames) {
        addEntry(zipOutputStream, fileName);
      }
    }
    return inputFile;
  }

  private static void addEntry(ZipOutputStream zipOutputStream, String fileName) throws IOException {
    ZipEntry entry = new ZipEntry(fileName);
    zipOutputStream.putNextEntry(entry);

    OutputStreamWriter writer = new OutputStreamWriter(zipOutputStream);
    writer.write(fileName);
    writer.flush();

    zipOutputStream.closeEntry();
  }
}
