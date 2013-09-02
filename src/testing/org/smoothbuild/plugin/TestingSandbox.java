package org.smoothbuild.plugin;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFileList;
import org.smoothbuild.testing.TestingFileSystem;

public class TestingSandbox implements Sandbox {
  private final TestingFileSystem fileSystem = new TestingFileSystem();
  private TestingFileList resultFileList;
  private TestingFile resultFile;

  @Override
  public TestingFileList resultFileList() {
    checkState(resultFile == null,
        "Cannot call resultFileList() when resultFile() has been called.");

    if (resultFileList == null) {
      resultFileList = new TestingFileList(fileSystem);
    }

    return resultFileList;
  }

  @Override
  public TestingFile resultFile(Path path) {
    checkState(resultFile == null, "Cannot call resultFile() twice.");
    checkState(resultFileList == null,
        "Cannot call resultFile() when resultFileList() has been called.");
    resultFile = new TestingFile(fileSystem, Path.rootPath(), path);
    return resultFile;
  }

  public TestingFileSystem fileSystem() {
    return fileSystem;
  }
}
