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

  public TestSandbox() {
    this(new TestFileSystem(), new TestProblemsListener());
  }

  public TestSandbox(TestFileSystem fileSystem, TestProblemsListener problemsListener) {
    super(fileSystem, SANDBOX_ROOT, problemsListener);
    this.fileSystem = fileSystem;
    this.problems = problemsListener;
  }

  @Override
  public TestFileSystem projectFileSystem() {
    return fileSystem;
  }

  public TestProblemsListener problems() {
    return problems;
  }
}
