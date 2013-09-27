package org.smoothbuild.testing.task;

import static org.smoothbuild.plugin.api.Path.path;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.task.SandboxImpl;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.message.TestMessageListener;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

import com.google.common.annotations.VisibleForTesting;

public class TestSandbox extends SandboxImpl {
  @VisibleForTesting
  static final Path SANDBOX_ROOT = path("sandbox");

  private final TestFileSystem fileSystem;
  private final TestMessageListener messages;
  private final TestFileSet resultFileSet;

  public TestSandbox() {
    this(new TestFileSystem());
  }

  public TestSandbox(TestFileSystem fileSystem) {
    this(fileSystem, new TestFileSystem(fileSystem, SANDBOX_ROOT), new TestMessageListener());
  }

  public TestSandbox(TestFileSystem fileSystem, TestFileSystem sandboxFileSystem,
      TestMessageListener messages) {
    super(fileSystem, sandboxFileSystem, messages);
    this.fileSystem = fileSystem;
    this.messages = messages;
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

  public TestMessageListener messages() {
    return messages;
  }
}
