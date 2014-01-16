package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.message.base.MessageType.ERROR;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.io.cache.value.build.SValueBuildersImpl;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.testing.io.cache.value.FakeValueDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class PluginApiImplTest {
  Task task = task();

  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeValueDb valueDb = new FakeValueDb(fileSystem);

  PluginApiImpl pluginApi = new PluginApiImpl(fileSystem, new SValueBuildersImpl(valueDb));

  @Test
  public void fileSystem() throws Exception {
    assertThat(pluginApi.projectFileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void reportedErrors() throws Exception {
    Message errorMessage = new Message(ERROR, "message");
    pluginApi.report(errorMessage);
    assertThat(pluginApi.messages()).containsOnly(errorMessage);
  }

  private static Task task() {
    Task task = mock(Task.class);
    Mockito.when(task.name()).thenReturn("name");
    Mockito.when(task.codeLocation()).thenReturn(new FakeCodeLocation());
    return task;
  }
}
