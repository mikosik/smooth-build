package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.HashedDb;
import org.smoothbuild.object.ObjectsDb;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.TestMessageGroup;

import com.google.common.annotations.VisibleForTesting;

public class TestSandbox extends SandboxImpl {
  @VisibleForTesting
  static final Path SANDBOX_ROOT = path("sandbox");

  private final FakeFileSystem fileSystem;
  private final FakeFileSystem sandboxFileSystem;
  private final TestMessageGroup messageGroup;

  public TestSandbox() {
    this(new FakeFileSystem());
  }

  public TestSandbox(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeFileSystem(fileSystem, SANDBOX_ROOT), new TestMessageGroup());
  }

  public TestSandbox(FakeFileSystem fileSystem, FakeFileSystem sandboxFileSystem,
      TestMessageGroup messageGroup) {
    super(fileSystem, sandboxFileSystem, new ObjectsDb(new HashedDb(fileSystem)), callLocation(
        simpleName("name"), codeLocation(1, 2, 4)), messageGroup);
    this.fileSystem = fileSystem;
    this.sandboxFileSystem = sandboxFileSystem;
    this.messageGroup = messageGroup;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeFileSystem sandboxFileSystem() {
    return sandboxFileSystem;
  }

  public TestMessageGroup messages() {
    return messageGroup;
  }
}
