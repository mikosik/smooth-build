package org.smoothbuild.testing.db.values;

import static org.smoothbuild.util.Lists.list;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Streams;

public class ValueCreators {
  public static <T extends Value> Array array(HashedDb hashedDb, Type elementType,
      Value... elements) {
    return array(new ValuesDb(hashedDb), elementType, elements);
  }

  public static <T extends Value> Array array(ValuesDb valuesDb, Type elementType,
      Value... elements) {
    return valuesDb.arrayBuilder(elementType).addAll(list(elements)).build();
  }

  public static Struct file(HashedDb hashedDb, Path path) {
    return file(new ValuesDb(hashedDb), path);
  }

  public static Struct file(ValuesDb valuesDb, Path path) {
    return file(valuesDb, path, path.value().getBytes(SmoothConstants.CHARSET));
  }

  public static Struct file(ValuesDb valuesDb, Path path, byte[] content) {
    SString string = valuesDb.string(path.value());
    Blob blob = blob(valuesDb, content);
    return valuesDb.file(string, blob);
  }

  public static Blob blob(ValuesDb valuesDb, byte[] bytes) {
    BlobBuilder builder = valuesDb.blobBuilder();
    try {
      Streams.copy(new ByteArrayInputStream(bytes), builder);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }
}
