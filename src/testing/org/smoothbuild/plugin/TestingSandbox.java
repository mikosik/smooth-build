package org.smoothbuild.plugin;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFileList;
import org.smoothbuild.testing.TestingFileSystem;
import org.smoothbuild.testing.problem.TestingProblemsListener;

public class TestingSandbox extends SandboxImpl {
  private final TestingFileSystem fileSystem;
  private final TestingProblemsListener problems;

  private TestingFileList resultFileList;
  private TestingFile resultFile;

  public TestingSandbox() {
    this(new TestingFileSystem(), new TestingProblemsListener());
  }

  public TestingSandbox(TestingFileSystem fileSystem, TestingProblemsListener problemsListener) {
    super(fileSystem, Path.rootPath(), problemsListener);
    this.fileSystem = fileSystem;
    this.problems = problemsListener;
  }

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

  @Override
  public TestingFileSystem fileSystem() {
    return fileSystem;
  }

  public TestingProblemsListener problems() {
    return problems;
  }
}
