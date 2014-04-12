package org.smoothbuild.lang.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.testing.common.ZipTester;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FakeBlob;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

import com.google.common.collect.Iterables;

public class UnzipperTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";
  String directoryName = "my/directory/";

  FakeNativeApi nativeApi = new FakeNativeApi();
  Unzipper unzipper = new Unzipper(nativeApi);

  @Test
  public void unzipping() throws Exception {
    FakeBlob zipBlob = zipped(fileName1, fileName2);

    SArray<SFile> resultFileArray = unzipper.unzip(zipBlob);

    int fileCount = 0;
    for (SFile file : resultFileArray) {
      fileCount++;
      assertContent(file.content().openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unzipperIgnoresDirectories() throws Exception {
    FakeBlob zipBlob = zipped(fileName1, directoryName);

    SArray<SFile> resultFileArray = unzipper.unzip(zipBlob);

    assertThat(Iterables.size(resultFileArray)).isEqualTo(1);
    assertThat(resultFileArray.iterator().next().path()).isEqualTo(path(fileName1));
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    FakeBlob zipBlob = zipped(illegalFileName);
    try {
      unzipper.unzip(zipBlob);
      fail("exception should be thrown");
    } catch (IllegalPathInZipError e) {
      // expected
    }
  }

  private static FakeBlob zipped(String... fileNames) throws IOException {
    FakeFileSystem fileSystem = new FakeFileSystem();
    Path path = ZipTester.zippedFiles(fileSystem, fileNames);
    return new FakeBlob(inputStreamToBytes(fileSystem.openInputStream(path)));
  }
}
