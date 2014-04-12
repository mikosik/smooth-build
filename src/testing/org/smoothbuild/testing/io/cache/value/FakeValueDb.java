package org.smoothbuild.testing.io.cache.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.cache.value.instance.CachedBlob;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;

public class FakeValueDb extends ValueDb {

  public FakeValueDb() {
    this(new FakeFileSystem());
  }

  public FakeValueDb(FileSystem fakeFileSystem) {
    this(new HashedDb(fakeFileSystem));
  }

  public FakeValueDb(HashedDb hashedDb) {
    super(hashedDb);
  }

  public SFile createFileContainingItsPath(Path path) {
    CachedBlob content = writeBlob(path.value().getBytes(CHARSET));
    return writeFile(path, content);
  }
}
