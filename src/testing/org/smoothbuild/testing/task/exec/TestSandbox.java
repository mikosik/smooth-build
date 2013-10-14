package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.testing.message.TestMessageGroup;
import org.smoothbuild.testing.type.impl.TestFileSet;

import com.google.common.annotations.VisibleForTesting;

public class TestSandbox extends SandboxImpl {
  @VisibleForTesting
  static final Path SANDBOX_ROOT = path("sandbox");

  private final TestFileSystem fileSystem;
  private final TestMessageGroup messageGroup;
  private final TestFileSet resultFileSet;

  public TestSandbox() {
    this(new TestFileSystem());
  }

  public TestSandbox(TestFileSystem fileSystem) {
    this(fileSystem, new TestFileSystem(fileSystem, SANDBOX_ROOT), new TestMessageGroup());
  }

  public TestSandbox(TestFileSystem fileSystem, TestFileSystem sandboxFileSystem,
      TestMessageGroup messageGroup) {
    super(fileSystem, sandboxFileSystem, callLocation(simpleName("name"), codeLocation(1, 2, 4)),
        messageGroup);
    this.fileSystem = fileSystem;
    this.messageGroup = messageGroup;
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

  public TestMessageGroup messages() {
    return messageGroup;
  }
}
