package org.smoothbuild.task.exec;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.common.Matchers.same;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.Message;

public class ContainerImplTest {
  private final FileSystem fileSystem = new MemoryFileSystem();
  private final TempManager tempDirProvider = mock(TempManager.class);

  private ContainerImpl containerImpl;
  private Message message;
  private TempDir tempDir;

  @Before
  public void before() {
    given(containerImpl = new ContainerImpl(fileSystem, memoryValuesDb(), tempDirProvider));
  }

  @Test
  public void file_system() throws Exception {
    when(containerImpl.projectFileSystem());
    thenReturned(same(fileSystem));
  }

  @Test
  public void messages_are_logged() throws Exception {
    given(message = new ErrorMessage("message"));
    when(containerImpl).log(message);
    then(containerImpl.messages(), contains(message));
  }

  @Test
  public void create_temp_dir_call_is_forwarded_to_temp_dir_manager() throws Exception {
    given(tempDir = mock(TempDir.class));
    given(willReturn(tempDir), tempDirProvider).tempDir();
    when(containerImpl).createTempDir();
    thenReturned(tempDir);
  }
}
