package org.smoothbuild.testing.io.cache.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

public class FakeValueDb extends ValueDb {
  public FakeValueDb() {
    this(new FakeFileSystem());
  }

  public FakeValueDb(FileSystem fakeFileSystem) {
    super(new HashedDb(fakeFileSystem));
  }

  public File createFileContainingItsPath(Path path) {
    return file(path, path.value().getBytes(CHARSET));
  }
}
