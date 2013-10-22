package org.smoothbuild.testing.object;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.object.HashedDb;
import org.smoothbuild.object.ObjectsDb;
import org.smoothbuild.testing.fs.base.FakeFileSystem;

public class FakeObjectsDb extends ObjectsDb {
  public FakeObjectsDb() {
    this(new FakeFileSystem());
  }

  public FakeObjectsDb(FileSystem fakeFileSystem) {
    super(new HashedDb(fakeFileSystem));
  }
}
