package org.smoothbuild.db.values;

import static org.smoothbuild.lang.type.ArrayType.arrayOf;
import static org.smoothbuild.lang.value.SString.storeStringInDb;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.StructBuilder;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.lang.value.ValueFactory;

import com.google.common.hash.HashCode;

public class ValuesDb implements ValueFactory {
  private final HashedDb hashedDb;
  private final TypeSystem typeSystem;

  @Inject
  public ValuesDb(@Values HashedDb hashedDb, TypeSystem typeSystem) {
    this.hashedDb = hashedDb;
    this.typeSystem = typeSystem;
  }

  public static ValuesDb memoryValuesDb() {
    MemoryFileSystem fileSystem = new MemoryFileSystem();
    return new ValuesDb(new HashedDb(fileSystem, Path.root(), new TempManager(fileSystem)),
        new TypeSystem());
  }

  public Types types() {
    return typeSystem;
  }

  @Override
  public ArrayBuilder arrayBuilder(Type elementType) {
    ArrayType arrayType = arrayOf(elementType);
    if (arrayType == null) {
      throw new IllegalArgumentException("Cannot create array with element of type " + elementType);
    }
    return createArrayBuilder(arrayType);
  }

  private ArrayBuilder createArrayBuilder(ArrayType type) {
    return new ArrayBuilder(type, hashedDb);
  }

  @Override
  public StructBuilder structBuilder(StructType type) {
    return new StructBuilder(type, hashedDb);
  }

  @Override
  public Struct file(SString path, Blob content) {
    return structBuilder(typeSystem.file())
        .set("content", content)
        .set("path", path)
        .build();
  }

  @Override
  public BlobBuilder blobBuilder() {
    return new BlobBuilder(typeSystem.blob(), hashedDb);
  }

  @Override
  public SString string(String string) {
    return storeStringInDb(typeSystem.string(), string, hashedDb);
  }

  public Array read(ArrayType type, HashCode hash) {
    return type.newValue(hash, hashedDb);
  }

  public Struct read(StructType type, HashCode hash) {
    return type.newValue(hash, hashedDb);
  }

  public Value read(Type type, HashCode hash) {
    return type.newValue(hash, hashedDb);
  }
}
