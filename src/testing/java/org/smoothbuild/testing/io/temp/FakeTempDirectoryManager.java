package org.smoothbuild.testing.io.temp;

import java.nio.file.Paths;

import javax.inject.Provider;

import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempDirectory;
import org.smoothbuild.io.util.TempDirectoryManager;
import org.smoothbuild.lang.base.ValueFactory;

public class FakeTempDirectoryManager extends TempDirectoryManager {
  public FakeTempDirectoryManager(ValueFactory valueFactory) {
    super(new FakeTempDirectoryProvider(valueFactory));
  }

  private static class FakeTempDirectoryProvider implements Provider<TempDirectory> {
    private final ValueFactory valueFactory;
    private int index;

    public FakeTempDirectoryProvider(ValueFactory valueFactory) {
      this.valueFactory = valueFactory;
      this.index = 0;
    }

    @Override
    public TempDirectory get() {
      java.nio.file.Path rootPath = Paths.get("/fake/temporary/path/" + index++);
      return new TempDirectory(valueFactory, rootPath, new MemoryFileSystem());
    }
  }
}
