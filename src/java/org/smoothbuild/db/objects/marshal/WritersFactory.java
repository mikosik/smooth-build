package org.smoothbuild.db.objects.marshal;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.Objects;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;

public class WritersFactory {
  private final HashedDb hashedDb;
  private final ReadersFactory readersFactory;

  @Inject
  public WritersFactory(@Objects HashedDb hashedDb, ReadersFactory readersFactory) {
    this.hashedDb = hashedDb;
    this.readersFactory = readersFactory;
  }

  public <T extends SValue> ArrayBuilder<T> arrayWriter(SArrayType<T> arrayType) {
    /*
     * Each cast is safe as it is preceded by checking arrayType.
     */
    if (arrayType == FILE_ARRAY) {
      return cast(createArrayBuilder(FILE_ARRAY));
    }
    if (arrayType == BLOB_ARRAY) {
      return cast(createArrayBuilder(BLOB_ARRAY));
    }
    if (arrayType == STRING_ARRAY) {
      return cast(createArrayBuilder(STRING_ARRAY));
    }
    if (arrayType == NIL) {
      return cast(new NilWriter(hashedDb, readersFactory.getReader(NOTHING)));
    }

    throw new IllegalArgumentException("Cannot create ArrayWriter for array type = " + arrayType);
  }

  @SuppressWarnings("unchecked")
  private static <T extends SValue> ArrayBuilder<T> cast(ArrayBuilder<?> arrayWriter) {
    return (ArrayBuilder<T>) arrayWriter;
  }

  private <T extends SValue> ArrayBuilder<T> createArrayBuilder(SArrayType<T> arrayType) {
    ObjectReader<T> reader = readersFactory.getReader(arrayType.elemType());
    return new ArrayWriter<T>(hashedDb, arrayType, reader);
  }

  public FileWriter fileWriter() {
    return new FileWriter(hashedDb);
  }

  public BlobWriter blobWriter() {
    return new BlobWriter(hashedDb);
  }
}
