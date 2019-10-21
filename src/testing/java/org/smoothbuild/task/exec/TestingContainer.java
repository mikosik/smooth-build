package org.smoothbuild.task.exec;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.TestingHashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.lang.runtime.TestingRuntimeTypes;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.ValueFactory;

public class TestingContainer extends Container {
  public TestingContainer() {
    this(new MemoryFileSystem(), new TestingHashedDb());
  }

  public TestingContainer(FileSystem fileSystem, HashedDb hashedDb) {
    this(fileSystem, hashedDb, new TypesDb(hashedDb));
  }

  public TestingContainer(FileSystem fileSystem, HashedDb hashedDb, TypesDb typesDb) {
    this(fileSystem, typesDb, new ValuesDb(hashedDb, typesDb));
  }

  public TestingContainer(FileSystem fileSystem, TypesDb typesDb, ValuesDb valuesDb) {
    this(fileSystem, new TestingRuntimeTypes(typesDb), valuesDb);
  }

  public TestingContainer(FileSystem fileSystem, RuntimeTypes types, ValuesDb valuesDb) {
    super(fileSystem, new ValueFactory(types, valuesDb), types, new TempManager(fileSystem));
  }
}
