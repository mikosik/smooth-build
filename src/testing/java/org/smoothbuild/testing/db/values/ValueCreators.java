package org.smoothbuild.testing.db.values;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Streams;

public class ValueCreators {
  public static <T extends Value> Array<T> array(ValuesDb valuesDb, Class<T> elementType,
      T... elements) {
    ArrayBuilder<T> arrayBuilder = valuesDb.arrayBuilder(elementType);
    for (T elem : elements) {
      arrayBuilder.add(elem);
    }
    return arrayBuilder.build();
  }

  public static SFile file(ValuesDb valuesDb, Path path) {
    return file(valuesDb, path, path.value().getBytes(SmoothConstants.CHARSET));
  }

  public static SFile file(ValuesDb valuesDb, Path path, String content) {
    return file(valuesDb, path, content.getBytes(SmoothConstants.CHARSET));
  }

  public static SFile file(ValuesDb valuesDb, Path path, byte[] bytes) {
    Blob blob = blob(valuesDb, bytes);
    return valuesDb.file(path, blob);
  }

  public static Blob blob(ValuesDb valuesDb, String content) {
    return blob(valuesDb, content.getBytes(SmoothConstants.CHARSET));
  }

  public static Blob blob(ValuesDb valuesDb, byte[] bytes) {
    BlobBuilder builder = valuesDb.blobBuilder();
    try {
      Streams.copy(new ByteArrayInputStream(bytes), builder.openOutputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }
}
