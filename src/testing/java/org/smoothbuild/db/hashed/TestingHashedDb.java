package org.smoothbuild.db.hashed;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;

public class TestingHashedDb extends HashedDb {
  public TestingHashedDb() {
    this(new MemoryFileSystem());
  }

  private TestingHashedDb(FileSystem fileSystem) {
    super(fileSystem, Path.root(), new TempManager(fileSystem));
  }
}
