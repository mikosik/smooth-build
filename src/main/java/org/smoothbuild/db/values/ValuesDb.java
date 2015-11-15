package org.smoothbuild.db.values;

import static org.smoothbuild.lang.type.Types.arrayElementJTypes;
import static org.smoothbuild.lang.type.Types.arrayTypeContaining;
import static org.smoothbuild.lang.type.Types.jTypeToType;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.marshal.ArrayMarshaller;
import org.smoothbuild.db.values.marshal.BlobMarshaller;
import org.smoothbuild.db.values.marshal.FileMarshaller;
import org.smoothbuild.db.values.marshal.NothingMarshaller;
import org.smoothbuild.db.values.marshal.ValueMarshaller;
import org.smoothbuild.db.values.marshal.StringMarshaller;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.lang.value.ValueFactory;

import com.google.common.hash.HashCode;
import com.google.inject.TypeLiteral;

public class ValuesDb implements ValueFactory {
  private final HashedDb hashedDb;

  @Inject
  public ValuesDb(@Values HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public static ValuesDb valuesDb() {
    return valuesDb(new MemoryFileSystem());
  }

  public static ValuesDb valuesDb(FileSystem fileSystem) {
    return new ValuesDb(new HashedDb(fileSystem));
  }

  @Override
  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> elementClass) {
    if (!(arrayElementJTypes().contains(TypeLiteral.get(elementClass)))) {
      throw new IllegalArgumentException("Illegal type " + elementClass.getCanonicalName());
    }
    Type type = jTypeToType(TypeLiteral.get(elementClass));
    return createArrayBuilder(arrayTypeContaining(type), elementClass);
  }

  private <T extends Value> ArrayBuilder<T> createArrayBuilder(ArrayType type,
      Class<?> elementClass) {
    ArrayMarshaller<T> marshaller = (ArrayMarshaller<T>) marshaller(type);
    return new ArrayBuilder<T>(marshaller, elementClass);
  }

  @Override
  public SFile file(Path path, Blob content) {
    return new FileMarshaller(hashedDb).write(path, content);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return new BlobBuilder(new BlobMarshaller(hashedDb));
  }

  @Override
  public SString string(String string) {
    return new StringMarshaller(hashedDb).write(string);
  }

  public Value read(Type type, HashCode hash) {
    return marshaller(type).read(hash);
  }

  private ValueMarshaller<?> marshaller(Type type) {
    if (type == Types.STRING) {
      return new StringMarshaller(hashedDb);
    }
    if (type == Types.BLOB) {
      return new BlobMarshaller(hashedDb);
    }
    if (type == Types.FILE) {
      return new FileMarshaller(hashedDb);
    }
    if (type == Types.NOTHING) {
      return new NothingMarshaller();
    }
    if (type instanceof ArrayType) {
      return arrayMarshaller((ArrayType) type);
    }
    throw new RuntimeException("Unexpected type: " + type);
  }

  private ArrayMarshaller<?> arrayMarshaller(ArrayType arrayType) {
    return new ArrayMarshaller<>(hashedDb, arrayType, marshaller(arrayType.elemType()));
  }
}
