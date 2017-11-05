package org.smoothbuild.testing.db.values;

import static java.util.Arrays.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Streams;

public class ValueCreators {
  public static <T extends Value> Array array(ValuesDb valuesDb, Type elementType,
      Value... elements) {
    ArrayBuilder arrayBuilder = valuesDb.arrayBuilder(elementType);
    stream(elements).forEach(arrayBuilder::add);
    return arrayBuilder.build();
  }

  public static SFile file(ValuesDb valuesDb, Path path) {
    return file(valuesDb, path, path.value().getBytes(SmoothConstants.CHARSET));
  }

  public static SFile file(ValuesDb valuesDb, Path path, byte[] content) {
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
