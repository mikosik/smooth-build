package org.smoothbuild.testing.db.objects;

import static org.smoothbuild.SmoothContants.CHARSET;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.objects.instance.BlobObject;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

public class FakeObjectsDb extends ObjectsDb {

  public FakeObjectsDb() {
    this(new FakeFileSystem());
  }

  public FakeObjectsDb(FileSystem fakeFileSystem) {
    this(new HashedDb(fakeFileSystem));
  }

  public FakeObjectsDb(HashedDb hashedDb) {
    super(hashedDb);
  }

  public SFile createFileContainingItsPath(Path path) {
    BlobObject content = writeBlob(path.value().getBytes(CHARSET));
    return writeFile(path, content);
  }
}
