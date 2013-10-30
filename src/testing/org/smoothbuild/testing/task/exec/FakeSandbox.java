package org.smoothbuild.testing.task.exec;

import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.db.FakeObjectDb;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.message.FakeMessageGroup;

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
    super(fileSystem, objectDb, new FakeCodeLocation(), messageGroup);
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
