package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.message.TestMessageListener;
import org.smoothbuild.testing.type.impl.TestFileSet;

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
    super(fileSystem, sandboxFileSystem, callLocation(simpleName("name"), codeLocation(1, 2, 4)),
        messages);
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
