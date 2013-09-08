package org.smoothbuild.plugin;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.problem.TestingProblemsListener;

public class TestingSandbox extends SandboxImpl {
  private final TestFileSystem fileSystem;
  private final TestingProblemsListener problems;

  public TestingSandbox() {
    this(new TestFileSystem(), new TestingProblemsListener());
  }

  public TestingSandbox(TestFileSystem fileSystem, TestingProblemsListener problemsListener) {
    super(fileSystem, Path.rootPath(), problemsListener);
    this.fileSystem = fileSystem;
    this.problems = problemsListener;
  }

  @Override
  public TestFileSystem projectFileSystem() {
    return fileSystem;
  }

  public TestingProblemsListener problems() {
    return problems;
  }
}
