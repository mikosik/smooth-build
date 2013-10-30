package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;

import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.object.FakeObjectDb;

public class FakeSandbox extends SandboxImpl {
  private final FakeFileSystem fileSystem;
  private final FakeMessageGroup messageGroup;
  private final FakeObjectDb objectDb;

  public FakeSandbox() {
    this(new FakeFileSystem());
  }

  public FakeSandbox(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeMessageGroup(), new FakeObjectDb(fileSystem));
  }

  public FakeSandbox(FakeFileSystem fileSystem, FakeMessageGroup messageGroup, FakeObjectDb objectDb) {
    super(fileSystem, objectDb, callLocation(simpleName("name"), new FakeCodeLocation()),
        messageGroup);
    this.fileSystem = fileSystem;
    this.messageGroup = messageGroup;
    this.objectDb = objectDb;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeObjectDb objectDb() {
    return objectDb;
  }

  public FakeMessageGroup messages() {
    return messageGroup;
  }
}
