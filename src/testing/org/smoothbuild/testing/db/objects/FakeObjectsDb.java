package org.smoothbuild.testing.db.objects;

import static org.smoothbuild.SmoothContants.CHARSET;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.util.Streams;

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
    SBlob content = writeBlob(path.value().getBytes(CHARSET));
    return fileBuilder().setPath(path).setContent(content).build();
  }

  private SBlob writeBlob(byte[] bytes) {
    BlobBuilder builder = blobBuilder();
    try {
      Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }
}
