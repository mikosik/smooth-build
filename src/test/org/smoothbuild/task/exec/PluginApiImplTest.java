package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.junit.Test;
import org.smoothbuild.io.cache.value.build.SValueBuildersImpl;
import org.smoothbuild.io.temp.TempDirectoryManager;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.io.temp.FakeTempDirectoryManager;

public class PluginApiImplTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeValueDb valueDb = new FakeValueDb(fileSystem);
  SValueBuilders valueBuilders = new SValueBuildersImpl(valueDb);
  TempDirectoryManager tempDirectoryManager = new FakeTempDirectoryManager(valueBuilders);

  PluginApiImpl pluginApi = new PluginApiImpl(fileSystem, valueBuilders, tempDirectoryManager);

  @Test
  public void fileSystem() throws Exception {
    assertThat(pluginApi.projectFileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void messages_are_logged() throws Exception {
    Message errorMessage = new Message(ERROR, "message");
    pluginApi.log(errorMessage);
    assertThat(pluginApi.loggedMessages()).containsOnly(errorMessage);
  }
}
