package org.smoothbuild.testing.io.temp;

import java.nio.file.Paths;

import javax.inject.Provider;

import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.temp.TempDirectory;
import org.smoothbuild.io.temp.TempDirectoryManager;
import org.smoothbuild.lang.base.SValueFactory;

public class FakeTempDirectoryManager extends TempDirectoryManager {
  public FakeTempDirectoryManager(SValueFactory valueBuilders) {
    super(new FakeTempDirectoryProvider(valueBuilders));
  }

  private static class FakeTempDirectoryProvider implements Provider<TempDirectory> {
    private final SValueFactory valueBuilders;
    private int index;

    public FakeTempDirectoryProvider(SValueFactory valueBuilders) {
      this.valueBuilders = valueBuilders;
      this.index = 0;
    }

    @Override
    public TempDirectory get() {
      java.nio.file.Path rootPath = Paths.get("/fake/temporary/path/" + index++);
      return new TempDirectory(valueBuilders, rootPath, new MemoryFileSystem());
    }
  };
}
