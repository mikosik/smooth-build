package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.object.FakeObjectsDb;

import com.google.common.annotations.VisibleForTesting;

public class FakeSandbox extends SandboxImpl {
  @VisibleForTesting
  static final Path SANDBOX_ROOT = path("sandbox");

  private final FakeFileSystem fileSystem;
  private final FakeFileSystem sandboxFileSystem;
  private final FakeMessageGroup messageGroup;

  public FakeSandbox() {
    this(new FakeFileSystem());
  }

  public FakeSandbox(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeFileSystem(fileSystem, SANDBOX_ROOT), new FakeMessageGroup());
  }

  public FakeSandbox(FakeFileSystem fileSystem, FakeFileSystem sandboxFileSystem,
      FakeMessageGroup messageGroup) {
    super(fileSystem, sandboxFileSystem, new FakeObjectsDb(fileSystem), callLocation(
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

  public FakeMessageGroup messages() {
    return messageGroup;
  }
}
