package org.smoothbuild.testing.io.fs.base;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.SubFileSystem;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;

public class FakeFileSystem extends SubFileSystem {

  public FakeFileSystem() {
    this(new MemoryFileSystem(), Path.rootPath());
  }

  public FakeFileSystem(FileSystem fileSystem, Path root) {
    super(fileSystem, root);
  }
}
