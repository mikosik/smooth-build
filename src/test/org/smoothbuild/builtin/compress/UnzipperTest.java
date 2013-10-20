package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.testing.type.impl.FakeFileSet;
import org.smoothbuild.type.api.File;

import com.google.common.collect.Iterables;

public class UnzipperTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";
  String directoryName = "my/directory/";

  FakeFileSet resultFileSet = new FakeFileSet();
  Unzipper unzipper = new Unzipper();

  @Test
  public void unzipping() throws Exception {
    FakeFile zipFile = zippedFiles(fileName1, fileName2);

    unzipper.unzipFile(zipFile, new FileSetBuilder(resultFileSet));

    int fileCount = 0;
    for (File file : resultFileSet) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unzipperIgnoresDirectories() throws Exception {
    FakeFile jarFile = zippedFiles(fileName1, directoryName);

    unzipper.unzipFile(jarFile, new FileSetBuilder(resultFileSet));

    assertThat(Iterables.size(resultFileSet)).isEqualTo(1);
    assertThat(resultFileSet.iterator().next().path()).isEqualTo(path(fileName1));
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    FakeFile zipFile = zippedFiles(illegalFileName);

    try {
      unzipper.unzipFile(zipFile, new FileSetBuilder(resultFileSet));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(IllegalPathInZipError.class);
    }
  }

  private static FakeFile zippedFiles(String... fileNames) throws IOException {
    FakeFile inputFile = new FakeFileSet().createFile(path("input.zip"));

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
