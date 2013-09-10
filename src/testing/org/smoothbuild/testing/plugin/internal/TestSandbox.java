package org.smoothbuild.testing.plugin.internal;

import static org.smoothbuild.plugin.api.Path.path;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.problem.TestProblemsListener;

import com.google.common.annotations.VisibleForTesting;

public class TestSandbox extends SandboxImpl {
  @VisibleForTesting
  static final Path SANDBOX_ROOT = path("sandbox");

  private final TestFileSystem fileSystem;
  private final TestProblemsListener problems;
  private final TestFileSet resultFileSet;

  public TestSandbox() {
    this(new TestFileSystem());
  }

  public TestSandbox(TestFileSystem fileSystem) {
    this(fileSystem, new TestFileSystem(fileSystem, SANDBOX_ROOT), new TestProblemsListener());
  }

  public TestSandbox(TestFileSystem fileSystem, TestFileSystem sandboxFileSystem,
      TestProblemsListener problemsListener) {
    super(fileSystem, sandboxFileSystem, problemsListener);
    this.fileSystem = fileSystem;
    this.problems = problemsListener;
    this.resultFileSet = new TestFileSet(sandboxFileSystem);
  }

  @Override
  public TestFileSet resultFileSet() {
    return resultFileSet;
  }

  @Override
  public TestFileSystem projectFileSystem() {
    return fileSystem;
  }

  public TestProblemsListener problems() {
    return problems;
  }
}
