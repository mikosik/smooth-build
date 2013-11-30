package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.exec.PluginApiImpl;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeMessageGroup;

public class FakePluginApi extends PluginApiImpl {
  private final FakeFileSystem fileSystem;
  private final FakeMessageGroup messageGroup;
  private final FakeValueDb objectDb;

  public FakePluginApi() {
    this(new FakeFileSystem());
  }

  public FakePluginApi(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeMessageGroup(), new FakeValueDb(fileSystem));
  }

  public FakePluginApi(FakeFileSystem fileSystem, FakeMessageGroup messageGroup,
      FakeValueDb objectDb) {
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

  public SValue emptyArray() {
    return arrayBuilder(EMPTY_ARRAY).build();
  }
}
