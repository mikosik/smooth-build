package org.smoothbuild.db.values;

import static org.smoothbuild.lang.type.Types.arrayOf;
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

  public ArrayBuilder arrayBuilder(Type elementType) {
    ArrayType arrayType = arrayOf(elementType);
    if (arrayType == null) {
      throw new IllegalArgumentException("Cannot create array with element of type " + elementType);
    }
    return createArrayBuilder(arrayType);
  }

  private ArrayBuilder createArrayBuilder(ArrayType type) {
    return new ArrayBuilder(type, valueConstructor(type.elemType()), hashedDb);
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

  public Array read(ArrayType type, HashCode hash) {
    return (Array) read((Type) type, hash);
  }

  public Value read(Type type, HashCode hash) {
    return valueConstructor(type).apply(hash);
  }

  private Function<HashCode, ? extends Value> valueConstructor(Type type) {
    if (type.equals(Types.STRING)) {
      return (hash) -> new SString(hash, hashedDb);
    }
    if (type.equals(Types.BLOB)) {
      return (hash) -> new Blob(hash, hashedDb);
    }
    if (type.equals(Types.FILE)) {
      return (hash) -> new SFile(hash, hashedDb);
    }
    if (type.equals(Types.NOTHING)) {
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

  private Array arrayMarshaller(ArrayType type,
      Function<HashCode, ? extends Value> valueConstructor, HashCode hash) {
    return new Array(hash, type, valueConstructor, hashedDb);
  }
}
