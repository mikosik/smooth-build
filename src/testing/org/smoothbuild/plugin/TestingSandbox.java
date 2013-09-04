package org.smoothbuild.plugin;

import org.smoothbuild.fs.plugin.FileImpl;
import org.smoothbuild.fs.plugin.SandboxImpl;
import org.smoothbuild.testing.TestingFileSystem;
import org.smoothbuild.testing.problem.TestingProblemsListener;

public class TestingSandbox extends SandboxImpl {
  private final TestingFileSystem fileSystem;
  private final TestingProblemsListener problems;

  public TestingSandbox() {
    this(new TestingFileSystem(), new TestingProblemsListener());
  }

  public TestingSandbox(TestingFileSystem fileSystem, TestingProblemsListener problemsListener) {
    super(fileSystem, Path.rootPath(), problemsListener);
    this.fileSystem = fileSystem;
    this.problems = problemsListener;
  }

  @Override
  public File createFile(Path path) {
    return new FileImpl(fileSystem, Path.rootPath(), path);
  }

  @Override
  public TestingFileSystem fileSystem() {
    return fileSystem;
  }

  public TestingProblemsListener problems() {
    return problems;
  }
}
