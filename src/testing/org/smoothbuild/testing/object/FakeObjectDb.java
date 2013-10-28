package org.smoothbuild.testing.object;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.HashedDb;
import org.smoothbuild.object.ValueDb;
import org.smoothbuild.plugin.File;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

import com.google.common.base.Charsets;

public class FakeObjectDb extends ValueDb {
  public FakeObjectDb() {
    this(new FakeFileSystem());
  }

  public FakeObjectDb(FileSystem fakeFileSystem) {
    super(new HashedDb(fakeFileSystem));
  }

  public File createFileContainingItsPath(Path path) {
    return file(path, path.value().getBytes(Charsets.UTF_8));
  }
}
