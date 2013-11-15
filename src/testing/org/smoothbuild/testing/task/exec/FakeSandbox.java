package org.smoothbuild.testing.task.exec;

import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeMessageGroup;

public class FakeSandbox extends SandboxImpl {
  private final FakeFileSystem fileSystem;
  private final FakeMessageGroup messageGroup;
  private final FakeValueDb objectDb;

  public FakeSandbox() {
    this(new FakeFileSystem());
  }

  public FakeSandbox(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeMessageGroup(), new FakeValueDb(fileSystem));
  }

  public FakeSandbox(FakeFileSystem fileSystem, FakeMessageGroup messageGroup, FakeValueDb objectDb) {
    super(fileSystem, objectDb, messageGroup);
    this.fileSystem = fileSystem;
    this.messageGroup = messageGroup;
    this.objectDb = objectDb;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeValueDb objectDb() {
    return objectDb;
  }

  public FakeMessageGroup messages() {
    return messageGroup;
  }
}
