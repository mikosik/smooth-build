package org.smoothbuild.testing.db.objects;

import static org.smoothbuild.SmoothContants.CHARSET;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.SmoothContants;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.db.objects.build.ObjectBuilders;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SValue;
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
    this(hashedDb, new ObjectMarshallers(hashedDb));
  }

  public FakeObjectsDb(HashedDb hashedDb, ObjectMarshallers objectMarshallers) {
    super(objectMarshallers, new ObjectBuilders(hashedDb, objectMarshallers));
  }

  @SuppressWarnings("unchecked")
  public <T extends SValue> SArray<T> array(SArrayType<T> type, T... elements) {
    ArrayBuilder<T> arrayBuilder = arrayBuilder(type);
    for (T elem : elements) {
      arrayBuilder.add(elem);
    }
    return arrayBuilder.build();
  }

  public SFile file(Path path) {
    return file(path, path.value().getBytes(SmoothContants.CHARSET));
  }

  public SFile file(Path path, String content) {
    return file(path, content.getBytes(SmoothContants.CHARSET));
  }

  public SFile file(Path path, byte[] bytes) {
    SBlob blob = blob(bytes);
    return file(path, blob);
  }

  public SFile createFileContainingItsPath(Path path) {
    SBlob content = blob(path.value().getBytes(CHARSET));
    return file(path, content);
  }

  public SBlob blob(String content) {
    return blob(content.getBytes(SmoothContants.CHARSET));
  }

  public SBlob blob(byte[] bytes) {
    BlobBuilder builder = blobBuilder();
    try {
      Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }

}
