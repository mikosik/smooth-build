package org.smoothbuild.plugin;

import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Test;
import org.smoothbuild.testing.TestingFileList;
import org.smoothbuild.testing.TestingFileSystem;

public class TestingSandboxTest {
  Path file = path("my/path");
  Path file2 = path("my/path2");

  TestingSandbox testingSandbox = new TestingSandbox();

  @Test
  public void resultFileIsCreatedOnFileSystem() throws Exception {
    testingSandbox.resultFile(file).createContentWithFilePath();
    testingSandbox.fileSystem().assertFileContainsItsPath(Path.rootPath(), file);
  }

  @Test
  public void cannotCallResultFileTwice() {
    testingSandbox.resultFile(file);
    try {
      testingSandbox.resultFile(file);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void cannotCallResultFileAfterCallingResultFileList() {
    testingSandbox.resultFileList();
    try {
      testingSandbox.resultFile(file);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void resultFilesAreCreatedOnFileSystem() throws Exception {
    TestingFileList resultFileList = testingSandbox.resultFileList();
    resultFileList.createFile(file).createContentWithFilePath();
    resultFileList.createFile(file2).createContentWithFilePath();

    TestingFileSystem fileSystem = testingSandbox.fileSystem();
    fileSystem.assertFileContainsItsPath(Path.rootPath(), file);
    fileSystem.assertFileContainsItsPath(Path.rootPath(), file2);

  }

  @Test
  public void cannotCallResultFileListAfterCallingResultFileTwice() {
    testingSandbox.resultFile(path("my/path"));
    try {
      testingSandbox.resultFileList();
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }
}
