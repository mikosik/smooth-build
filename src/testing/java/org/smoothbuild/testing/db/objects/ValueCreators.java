package org.smoothbuild.testing.db.objects;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Streams;

public class ValueCreators {
  public static <T extends Value> Array<T> array(ObjectsDb objectsDb, Class<T> elementType,
      T... elements) {
    ArrayBuilder<T> arrayBuilder = objectsDb.arrayBuilder(elementType);
    for (T elem : elements) {
      arrayBuilder.add(elem);
    }
    return arrayBuilder.build();
  }

  public static SFile file(ObjectsDb objectsDb, Path path) {
    return file(objectsDb, path, path.value().getBytes(SmoothConstants.CHARSET));
  }

  public static SFile file(ObjectsDb objectsDb, Path path, String content) {
    return file(objectsDb, path, content.getBytes(SmoothConstants.CHARSET));
  }

  public static SFile file(ObjectsDb objectsDb, Path path, byte[] bytes) {
    Blob blob = blob(objectsDb, bytes);
    return objectsDb.file(path, blob);
  }

  public static Blob blob(ObjectsDb objectsDb, String content) {
    return blob(objectsDb, content.getBytes(SmoothConstants.CHARSET));
  }

  public static Blob blob(ObjectsDb objectsDb, byte[] bytes) {
    BlobBuilder builder = objectsDb.blobBuilder();
    try {
      Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }
}
