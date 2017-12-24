package org.smoothbuild.testing.db.values;

import static java.util.Arrays.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.smoothbuild.SmoothConstants;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.util.Streams;

public class ValueCreators {
  public static <T extends Value> Array array(HashedDb hashedDb, Type elementType,
      Value... elements) {
    return array(valuesDb(hashedDb), elementType, elements);
  }

  public static <T extends Value> Array array(ValuesDb valuesDb, Type elementType,
      Value... elements) {
    ArrayBuilder arrayBuilder = valuesDb.arrayBuilder(elementType);
    stream(elements).forEach(arrayBuilder::add);
    return arrayBuilder.build();
  }

  public static Struct file(HashedDb hashedDb, Path path) {
    return file(valuesDb(hashedDb), path);
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

  private static ValuesDb valuesDb(HashedDb hashedDb) {
    TypeSystem typeSystem = new TypeSystem(new TypesDb(hashedDb));
    return new ValuesDb(hashedDb, typeSystem);
  }
}
