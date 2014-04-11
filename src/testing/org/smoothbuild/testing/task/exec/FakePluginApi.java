package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;

import org.smoothbuild.io.cache.value.build.SValueBuildersImpl;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SNothing;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.exec.PluginApiImpl;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.io.temp.FakeTempDirectoryManager;
import org.smoothbuild.testing.message.FakeLoggedMessages;

public class FakePluginApi extends PluginApiImpl {
  private final FakeFileSystem fileSystem;
  private final FakeLoggedMessages messages;
  private final FakeValueDb valueDb;

  public FakePluginApi() {
    this(new FakeFileSystem());
  }

  private FakePluginApi(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeValueDb(fileSystem));
  }

  private FakePluginApi(FakeFileSystem fileSystem, FakeValueDb valueDb) {
    this(fileSystem, new SValueBuildersImpl(valueDb), valueDb);
  }

  public FakePluginApi(FakeFileSystem fileSystem, SValueBuilders sValueBuilders, FakeValueDb valueDb) {
    super(fileSystem, sValueBuilders, new FakeTempDirectoryManager(sValueBuilders));
    this.fileSystem = fileSystem;
    this.messages = new FakeLoggedMessages();
    this.valueDb = valueDb;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeValueDb valueDb() {
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
