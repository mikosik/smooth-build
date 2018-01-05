package org.smoothbuild.task.exec;

import static org.hamcrest.Matchers.contains;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.common.Matchers.same;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.type.TypesDb;

public class ContainerTest {
  private final FileSystem fileSystem = new MemoryFileSystem();
  private final TempManager tempDirProvider = mock(TempManager.class);

  private Container container;
  private Message message;
  private TempDir tempDir;

  @Before
  public void before() {
    HashedDb hashedDb = new HashedDb();
    TypesDb typesDb = new TypesDb(hashedDb);
    ValuesDb valuesDb = new ValuesDb(hashedDb, typesDb);
    container = new Container(fileSystem, valuesDb, typesDb, tempDirProvider);
  }

  @Test
  public void file_system() throws Exception {
    when(container.fileSystem());
    thenReturned(same(fileSystem));
  }

  @Test
  public void messages_are_logged() throws Exception {
    given(message = new ErrorMessage("message"));
    when(container).log(message);
    then(container.messages(), contains(message));
  }

  @Test
  public void create_temp_dir_call_is_forwarded_to_temp_dir_manager() throws Exception {
    given(tempDir = mock(TempDir.class));
    given(willReturn(tempDir), tempDirProvider).tempDir(any(ValuesDb.class));
    when(container).createTempDir();
    thenReturned(tempDir);
  }
}
