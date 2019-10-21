package org.smoothbuild.task.exec;

import static org.hamcrest.Matchers.equalTo;
import static org.smoothbuild.lang.message.Messages.severity;
import static org.smoothbuild.lang.message.Messages.text;
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
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDir;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.lang.value.ValueFactory;

import com.google.common.collect.Iterables;

public class ContainerTest {
  private final FileSystem fileSystem = new MemoryFileSystem();
  private final TempManager tempDirProvider = mock(TempManager.class);
  private Container container;
  private TempDir tempDir;

  @Before
  public void before() {
    HashedDb hashedDb = new TestingHashedDb();
    TypesDb typesDb = new TypesDb(hashedDb);
    ValuesDb valuesDb = new ValuesDb(hashedDb, typesDb);
    TestingRuntimeTypes types = new TestingRuntimeTypes(typesDb);
    ValueFactory valueFactory = new ValueFactory(types, valuesDb);
    container = new Container(fileSystem, valueFactory, types, tempDirProvider);
  }

  @Test
  public void file_system() {
    when(container.fileSystem());
    thenReturned(same(fileSystem));
  }

  @Test
  public void messages_are_logged() {
    when(container.log()).error("message");
    then(Iterables.size(container.messages().asIterable(Value.class)), equalTo(1));
    then(text(container.messages().asIterable(Struct.class).iterator().next()),
        equalTo("message"));
    then(severity(container.messages().asIterable(Struct.class).iterator().next()),
        equalTo("ERROR"));
  }

  @Test
  public void create_temp_dir_call_is_forwarded_to_temp_dir_manager() throws Exception {
    given(tempDir = mock(TempDir.class));
    given(willReturn(tempDir), tempDirProvider).tempDir(container);
    when(container).createTempDir();
    thenReturned(tempDir);
  }
}
