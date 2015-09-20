package org.smoothbuild.testing.db.objects;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Streams;

public class FakeObjectsDb extends ObjectsDb {
  public FakeObjectsDb() {
    this(new MemoryFileSystem());
  }

  public FakeObjectsDb(FileSystem fileSystem) {
    this(new HashedDb(fileSystem));
  }

  public FakeObjectsDb(HashedDb hashedDb) {
    this(hashedDb, new ObjectMarshallers(hashedDb));
  }

  public FakeObjectsDb(HashedDb hashedDb, ObjectMarshallers objectMarshallers) {
    super(objectMarshallers);
  }

  public <T extends Value> Array<T> array(Class<T> elementType, T... elements) {
    ArrayBuilder<T> arrayBuilder = arrayBuilder(elementType);
    for (T elem : elements) {
      arrayBuilder.add(elem);
    }
    return arrayBuilder.build();
  }

  public SFile file(Path path) {
    return file(path, path.value().getBytes(SmoothConstants.CHARSET));
  }

  public SFile file(Path path, String content) {
    return file(path, content.getBytes(SmoothConstants.CHARSET));
  }

  public SFile file(Path path, byte[] bytes) {
    Blob blob = blob(bytes);
    return file(path, blob);
  }

  public Blob blob(String content) {
    return blob(content.getBytes(SmoothConstants.CHARSET));
  }

  public Blob blob(byte[] bytes) {
    BlobBuilder builder = blobBuilder();
    try {
      Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }
}
