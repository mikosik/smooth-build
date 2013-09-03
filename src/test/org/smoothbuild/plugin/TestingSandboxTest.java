package org.smoothbuild.plugin;

import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.Path.path;

import org.junit.Test;
import org.smoothbuild.testing.TestingFileSet;
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
  public void cannotCallResultFileAfterCallingResultFileSet() {
    testingSandbox.resultFileSet();
    try {
      testingSandbox.resultFile(file);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void resultFilesAreCreatedOnFileSystem() throws Exception {
    TestingFileSet resultFileSet = testingSandbox.resultFileSet();
    resultFileSet.createFile(file).createContentWithFilePath();
    resultFileSet.createFile(file2).createContentWithFilePath();

    TestingFileSystem fileSystem = testingSandbox.fileSystem();
    fileSystem.assertFileContainsItsPath(Path.rootPath(), file);
    fileSystem.assertFileContainsItsPath(Path.rootPath(), file2);

  }

  @Test
  public void cannotCallResultFileSetAfterCallingResultFileTwice() {
    testingSandbox.resultFile(path("my/path"));
    try {
      testingSandbox.resultFileSet();
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }
}
