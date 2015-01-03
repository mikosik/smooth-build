package org.smoothbuild.testing.task.exec;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.io.temp.FakeTempDirectoryManager;
import org.smoothbuild.testing.message.FakeLoggedMessages;

public class FakeNativeApi extends NativeApiImpl {
  private final FakeFileSystem fileSystem;
  private final FakeLoggedMessages messages;
  private final FakeObjectsDb objectsDb;

  public FakeNativeApi() {
    this(new FakeFileSystem());
  }

  private FakeNativeApi(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeObjectsDb(fileSystem));
  }

  public FakeNativeApi(FakeFileSystem fileSystem, FakeObjectsDb objectsDb) {
    super(fileSystem, objectsDb, new FakeTempDirectoryManager(objectsDb));
    this.fileSystem = fileSystem;
    this.messages = new FakeLoggedMessages();
    this.objectsDb = objectsDb;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeObjectsDb objectsDb() {
    return objectsDb;
  }

  @Override
  public FakeLoggedMessages messages() {
    return messages;
  }

  @Override
  public void log(Message message) {
    messages.log(message);
  }

  public SFile file(Path path) {
    return objectsDb.file(path);
  }

  public SFile file(Path path, String content) {
    return objectsDb.file(path, content);
  }
}
