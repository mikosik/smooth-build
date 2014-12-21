package org.smoothbuild.testing.io.temp;

import java.nio.file.Paths;

import javax.inject.Provider;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.io.util.TempDirectoryManager;

public class FakeTempDirectoryManager extends TempDirectoryManager {
  public FakeTempDirectoryManager(ObjectsDb objectsDb) {
    super(new FakeTempDirectoryProvider(objectsDb));
  }

  private static class FakeTempDirectoryProvider implements Provider<TempDirectory> {
    private final ObjectsDb objectsDb;
    private int index;

    public FakeTempDirectoryProvider(ObjectsDb objectsDb) {
      this.objectsDb = objectsDb;
      this.index = 0;
    }

    @Override
    public TempDirectory get() {
      java.nio.file.Path rootPath = Paths.get("/fake/temporary/path/" + index++);
      return new TempDirectory(objectsDb, rootPath, new MemoryFileSystem());
    }
  }
}
