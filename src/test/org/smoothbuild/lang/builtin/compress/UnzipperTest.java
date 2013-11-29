package org.smoothbuild.lang.builtin.compress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToBytes;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.builtin.compress.err.IllegalPathInZipError;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.common.ZipTester;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.task.exec.FakePluginApi;

import com.google.common.collect.Iterables;

public class UnzipperTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";
  String directoryName = "my/directory/";

  FakePluginApi pluginApi = new FakePluginApi();
  Unzipper unzipper = new Unzipper(pluginApi);

  @Test
  public void unzipping() throws Exception {
    FakeFile zipFile = zipped(fileName1, fileName2);

    SArray<SFile> resultFileArray = unzipper.unzipFile(zipFile.content());

    int fileCount = 0;
    for (SFile file : resultFileArray) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unzipperIgnoresDirectories() throws Exception {
    FakeFile zipFile = zipped(fileName1, directoryName);

    SArray<SFile> resultFileArray = unzipper.unzipFile(zipFile.content());

    assertThat(Iterables.size(resultFileArray)).isEqualTo(1);
    assertThat(resultFileArray.iterator().next().path()).isEqualTo(path(fileName1));
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    FakeFile file = zipped(illegalFileName);
    try {
      unzipper.unzipFile(file.content());
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
