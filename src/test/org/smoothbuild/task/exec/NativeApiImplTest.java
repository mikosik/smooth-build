package org.smoothbuild.task.exec;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.objects.build.ObjectBuilders;
import org.smoothbuild.io.temp.TempDirectory;
import org.smoothbuild.io.temp.TempDirectoryManager;
import org.smoothbuild.lang.base.SValueBuilders;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

public class NativeApiImplTest {
  FakeFileSystem fileSystem = new FakeFileSystem();
  FakeObjectsDb valueDb = new FakeObjectsDb(fileSystem);
  SValueBuilders valueBuilders = new ObjectBuilders(valueDb);
  TempDirectoryManager tempDirectoryManager = mock(TempDirectoryManager.class);

  NativeApiImpl nativeApi = new NativeApiImpl(fileSystem, valueBuilders, tempDirectoryManager);

  @Test
  public void fileSystem() throws Exception {
    assertThat(nativeApi.projectFileSystem()).isSameAs(fileSystem);
  }

  @Test
  public void messages_are_logged() throws Exception {
    Message errorMessage = new Message(ERROR, "message");
    nativeApi.log(errorMessage);
    assertThat(nativeApi.loggedMessages()).containsOnly(errorMessage);
  }

  @Test
  public void create_temp_directory_call_is_forwarded_to_temp_directory_manager() throws Exception {
    TempDirectory tempDirectory = mock(TempDirectory.class);
    given(willReturn(tempDirectory), tempDirectoryManager).createTempDirectory();
    assertThat(nativeApi.createTempDirectory()).isSameAs(tempDirectory);
  }
}
