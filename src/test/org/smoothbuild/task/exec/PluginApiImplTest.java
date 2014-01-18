package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.io.cache.value.build.SValueBuildersImpl;
import org.smoothbuild.io.temp.TempDirectory;
import org.smoothbuild.io.temp.TempDirectoryManager;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

public class PluginApiImplTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeValueDb valueDb = new FakeValueDb(fileSystem);
  SValueBuilders valueBuilders = new SValueBuildersImpl(valueDb);
  TempDirectoryManager tempDirectoryManager = mock(TempDirectoryManager.class);

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

  @Test
  public void create_temp_directory_call_is_forwarded_to_temp_directory_manager() throws Exception {
    TempDirectory tempDirectory = mock(TempDirectory.class);
    Mockito.when(tempDirectoryManager.createTempDirectory()).thenReturn(tempDirectory);
    assertThat(pluginApi.createTempDirectory()).isSameAs(tempDirectory);
  }
}
