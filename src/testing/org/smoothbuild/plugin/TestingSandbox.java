package org.smoothbuild.plugin;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.testing.TestingFile;
import org.smoothbuild.testing.TestingFileSet;
import org.smoothbuild.testing.TestingFileSystem;
import org.smoothbuild.testing.problem.TestingProblemsListener;

public class TestingSandbox extends SandboxImpl {
  private final TestingFileSystem fileSystem;
  private final TestingProblemsListener problems;

  private TestingFileSet resultFileSet;
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
  public TestingFileSet resultFileSet() {
    checkState(resultFile == null, "Cannot call resultFileSet() when resultFile() has been called.");

    if (resultFileSet == null) {
      resultFileSet = new TestingFileSet(fileSystem);
    }

    return resultFileSet;
  }

  @Override
  public TestingFile resultFile(Path path) {
    checkState(resultFile == null, "Cannot call resultFile() twice.");
    checkState(resultFileSet == null,
        "Cannot call resultFile() when resultFileSet() has been called.");
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
