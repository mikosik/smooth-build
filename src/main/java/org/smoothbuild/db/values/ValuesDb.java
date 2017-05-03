package org.smoothbuild.db.values;

import static org.smoothbuild.lang.type.Types.arrayElementJTypes;
import static org.smoothbuild.lang.type.Types.arrayOf;
import static org.smoothbuild.lang.type.Types.jTypeToType;
import static org.smoothbuild.lang.value.SFile.storeFileInDb;
import static org.smoothbuild.lang.value.SString.storeStringInDb;

import java.util.function.Function;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.Array;
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

  public static ValuesDb memoryValuesDb() {
    MemoryFileSystem fileSystem = new MemoryFileSystem();
    return new ValuesDb(new HashedDb(fileSystem, Path.root(), new TempManager(fileSystem)));
  }

  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> elementClass) {
    if (!(arrayElementJTypes().contains(TypeLiteral.get(elementClass)))) {
      throw new IllegalArgumentException("Illegal type " + elementClass.getCanonicalName());
    }
    Type type = jTypeToType(TypeLiteral.get(elementClass));
    return createArrayBuilder(arrayOf(type), elementClass);
  }

  private <T extends Value> ArrayBuilder<T> createArrayBuilder(ArrayType type,
      Class<?> elementClass) {
    return new ArrayBuilder<T>(type, (Function<HashCode, T>) valueConstructor(type.elemType()),
        hashedDb);
  }

  public SFile file(SString path, Blob content) {
    return storeFileInDb(path, content, hashedDb);
  }

  public BlobBuilder blobBuilder() {
    return new BlobBuilder(hashedDb);
  }

  public SString string(String string) {
    return storeStringInDb(string, hashedDb);
  }

  public Value read(Type type, HashCode hash) {
    return valueConstructor(type).apply(hash);
  }

  private Function<HashCode, ? extends Value> valueConstructor(Type type) {
    if (type == Types.STRING) {
      return (hash) -> new SString(hash, hashedDb);
    }
    if (type == Types.BLOB) {
      return (hash) -> new Blob(hash, hashedDb);
    }
    if (type == Types.FILE) {
      return (hash) -> new SFile(hash, hashedDb);
    }
    if (type == Types.NOTHING) {
      return (hash) -> {
        throw new UnsupportedOperationException("Nothing cannot be constructed.");
      };
    }
    if (type instanceof ArrayType) {
      ArrayType arrayType = (ArrayType) type;
      return (hash) -> arrayMarshaller(arrayType, valueConstructor(arrayType.elemType()), hash);
    }
    throw new RuntimeException("Unexpected type: " + type);
  }

  private <T extends Value> Array<T> arrayMarshaller(ArrayType type,
      Function<HashCode, T> valueConstructor, HashCode hash) {
    return new Array<T>(hash, type, valueConstructor, hashedDb);
  }
}
