package org.smoothbuild.testing.task.exec;

import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;

import org.smoothbuild.io.cache.value.build.SValueBuildersImpl;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.exec.PluginApiImpl;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeMessageGroup;

public class FakePluginApi extends PluginApiImpl {
  private final FakeFileSystem fileSystem;
  private final FakeMessageGroup messageGroup;
  private final FakeValueDb valueDb;

  public FakePluginApi() {
    this(new FakeFileSystem());
  }

  private FakePluginApi(FakeFileSystem fileSystem) {
    this(fileSystem, new FakeMessageGroup(), new FakeValueDb(fileSystem));
  }

  private FakePluginApi(FakeFileSystem fileSystem, FakeMessageGroup messageGroup,
      FakeValueDb valueDb) {
    super(fileSystem, new SValueBuildersImpl(valueDb), messageGroup);
    this.fileSystem = fileSystem;
    this.messageGroup = messageGroup;
    this.valueDb = valueDb;
  }

  @Override
  public FakeFileSystem projectFileSystem() {
    return fileSystem;
  }

  public FakeValueDb valueDb() {
    return valueDb;
  }

  public FakeMessageGroup messages() {
    return messageGroup;
  }

  public SValue emptyArray() {
    return arrayBuilder(EMPTY_ARRAY).build();
  }
}
