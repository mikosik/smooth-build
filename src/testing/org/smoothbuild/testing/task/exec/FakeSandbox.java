package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.smoothbuild.task.exec.SandboxImpl;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeMessageGroup;
import org.smoothbuild.testing.object.FakeObjectsDb;

public class FakeSandbox extends SandboxImpl {
  private final FakeFileSystem fileSystem;
  private final FakeMessageGroup messageGroup;
  private final FakeObjectsDb objectsDb;

  public FakeSandbox() {
    this(new FakeFileSystem());
  }

  public FakeSandbox(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeMessageGroup(), new FakeObjectsDb(fileSystem));
  }

  public FakeSandbox(FakeFileSystem fileSystem, FakeMessageGroup messageGroup,
      FakeObjectsDb objectsDb) {
    super(fileSystem, objectsDb, callLocation(simpleName("name"), codeLocation(1, 2, 4)),
        messageGroup);
    this.fileSystem = fileSystem;
    this.messageGroup = messageGroup;
    this.objectsDb = objectsDb;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeObjectsDb objectDb() {
    return objectsDb;
  }

  public FakeMessageGroup messages() {
    return messageGroup;
  }
}
