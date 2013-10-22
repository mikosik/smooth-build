package org.smoothbuild.testing.object;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.HashedDb;
import org.smoothbuild.object.ObjectsDb;
import org.smoothbuild.testing.fs.base.FakeFileSystem;
import org.smoothbuild.type.api.File;

import com.google.common.base.Charsets;

public class FakeObjectsDb extends ObjectsDb {
  public FakeObjectsDb() {
    this(new FakeFileSystem());
  }

  public FakeObjectsDb(FileSystem fakeFileSystem) {
    super(new HashedDb(fakeFileSystem));
  }

  public File createFileContainingItsPath(Path path) {
    return file(path, path.value().getBytes(Charsets.UTF_8));
  }
}
