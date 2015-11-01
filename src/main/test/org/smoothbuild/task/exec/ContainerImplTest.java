package org.smoothbuild.task.exec;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.lang.message.MessageType.ERROR;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.common.Matchers.same;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.lang.message.Message;

public class ContainerImplTest {
  private final FileSystem fileSystem = new MemoryFileSystem();
  private final Provider<TempDirectory> tempDirectoryProvider = mock(Provider.class);

  private ContainerImpl containerImpl;
  private Message message;
  private TempDirectory tempDirectory;

  @Before
  public void before() {
    given(containerImpl = new ContainerImpl(fileSystem, valuesDb(), tempDirectoryProvider));
  }

  @Test
  public void file_system() throws Exception {
    when(containerImpl.projectFileSystem());
    thenReturned(same(fileSystem));
  }

  @Test
  public void messages_are_logged() throws Exception {
    given(message = new Message(ERROR, "message"));
    when(containerImpl).log(message);
    then(containerImpl.messages(), contains(message));
  }

  @Test
  public void create_temp_directory_call_is_forwarded_to_temp_directory_manager() throws Exception {
    given(tempDirectory = mock(TempDirectory.class));
    given(willReturn(tempDirectory), tempDirectoryProvider).get();
    when(containerImpl).createTempDirectory();
    thenReturned(tempDirectory);
  }
}
