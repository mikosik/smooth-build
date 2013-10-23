package org.smoothbuild.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.common.ZipTester;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.task.exec.FakeSandbox;
import org.smoothbuild.testing.type.impl.FakeFile;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

import com.google.common.collect.Iterables;

public class UnzipperTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";
  String directoryName = "my/directory/";

  FakeSandbox sandbox = new FakeSandbox();
  Unzipper unzipper = new Unzipper(sandbox);

  @Test
  public void unzipping() throws Exception {
    FakeFile zipFile = zipped(fileName1, fileName2);

    FileSet resultFileSet = unzipper.unzipFile(zipFile);

    int fileCount = 0;
    for (File file : resultFileSet) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unzipperIgnoresDirectories() throws Exception {
    FakeFile zipFile = zipped(fileName1, directoryName);

    FileSet resultFileSet = unzipper.unzipFile(zipFile);

    assertThat(Iterables.size(resultFileSet)).isEqualTo(1);
    assertThat(resultFileSet.iterator().next().path()).isEqualTo(path(fileName1));
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    FakeFile file = zipped(illegalFileName);
    try {
      unzipper.unzipFile(file);
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(IllegalPathInZipError.class);
    }
  }

  private static FakeFile zipped(String... fileNames) throws IOException {
    FakeFileSystem fileSystem = new FakeFileSystem();
    Path path = ZipTester.zippedFiles(fileSystem, fileNames);
    return new FakeFile(path, inputStreamToBytes(fileSystem.openInputStream(path)));
  }
}
