package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.lang.base.STypes.EMPTY_ARRAY;

import org.smoothbuild.db.objects.build.ObjectBuilders;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SValueBuilders;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.NativeApiImpl;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.io.temp.FakeTempDirectoryManager;
import org.smoothbuild.testing.message.FakeLoggedMessages;

public class FakeNativeApi extends NativeApiImpl {
  private final FakeFileSystem fileSystem;
  private final FakeLoggedMessages messages;
  private final FakeObjectsDb valueDb;

  public FakeNativeApi() {
    this(new FakeFileSystem());
  }

  private FakeNativeApi(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeObjectsDb(fileSystem));
  }

  private FakeNativeApi(FakeFileSystem fileSystem, FakeObjectsDb valueDb) {
    this(fileSystem, new ObjectBuilders(valueDb), valueDb);
  }

  public FakeNativeApi(FakeFileSystem fileSystem, SValueBuilders sValueBuilders, FakeObjectsDb valueDb) {
    super(fileSystem, sValueBuilders, new FakeTempDirectoryManager(sValueBuilders));
    this.fileSystem = fileSystem;
    this.messages = new FakeLoggedMessages();
    this.valueDb = valueDb;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeObjectsDb valueDb() {
    return valueDb;
  }

  @Override
  public FakeLoggedMessages loggedMessages() {
    return messages;
  }

  @Override
  public void log(Message message) {
    messages.log(message);
  }

  public SArray<SNothing> emptyArray() {
    return arrayBuilder(EMPTY_ARRAY).build();
  }
}
