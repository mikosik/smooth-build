package org.smoothbuild.testing.db.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.db.value.ValueDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

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
