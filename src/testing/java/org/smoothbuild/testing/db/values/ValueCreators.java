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
import org.smoothbuild.lang.value.ValueFactory;
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
    return file(new ValueFactory(hashedDb), path);
  }

  public static Struct file(Path path) {
    return file(new ValueFactory(), path);
  }

  public static Struct file(ValueFactory valueFactory, Path path) {
    return file(valueFactory, path, path.value().getBytes(SmoothConstants.CHARSET));
  }

  public static Struct file(Path path, byte[] content) {
    ValueFactory valueFactory = new ValueFactory();
    SString string = valueFactory.string(path.value());
    Blob blob = blob(valueFactory, content);
    return valueFactory.file(string, blob);
  }

  public static Struct file(ValueFactory valueFactory, Path path, byte[] content) {
    SString string = valueFactory.string(path.value());
    Blob blob = blob(valueFactory, content);
    return valueFactory.file(string, blob);
  }

  public static Blob blob(byte[] bytes) {
    return blob(new ValueFactory(), bytes);
  }

  public static Blob blob(ValueFactory valueFactory, byte[] bytes) {
    BlobBuilder builder = valueFactory.blobBuilder();
    try {
      Streams.copy(new ByteArrayInputStream(bytes), builder);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }
}
