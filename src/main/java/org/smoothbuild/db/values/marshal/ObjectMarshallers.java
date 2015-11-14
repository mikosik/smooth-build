package org.smoothbuild.db.values.marshal;

import static org.smoothbuild.lang.type.Types.BLOB;
import static org.smoothbuild.lang.type.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.NOTHING;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.values.Values;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Nothing;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ObjectMarshallers {
  private final StringMarshaller stringMarshaller;
  private final BlobMarshaller blobMarshaller;
  private final FileMarshaller fileMarshaller;
  private final NothingMarshaller nothingMarshaller;

  private final ArrayMarshaller<SString> stringArrayMarshaller;
  private final ArrayMarshaller<Blob> blobArrayMarshaller;
  private final ArrayMarshaller<SFile> fileArrayMarshaller;
  private final ArrayMarshaller<Nothing> nilMarshaller;

  private final ImmutableMap<Type, ObjectMarshaller<?>> marshallersMap;
  private final ImmutableMap<ArrayType, ArrayMarshaller<?>> arrayMarshallersMap;

  @Inject
  public ObjectMarshallers(@Values HashedDb hashedDb) {
    this.stringMarshaller = new StringMarshaller(hashedDb);
    this.blobMarshaller = new BlobMarshaller(hashedDb);
    this.fileMarshaller = new FileMarshaller(hashedDb);
    this.nothingMarshaller = new NothingMarshaller();

    this.stringArrayMarshaller = new ArrayMarshaller<>(hashedDb, STRING_ARRAY, stringMarshaller);
    this.blobArrayMarshaller = new ArrayMarshaller<>(hashedDb, BLOB_ARRAY, blobMarshaller);
    this.fileArrayMarshaller = new ArrayMarshaller<>(hashedDb, FILE_ARRAY, fileMarshaller);
    this.nilMarshaller = new ArrayMarshaller<>(hashedDb, NIL, nothingMarshaller);

    Builder<Type, ObjectMarshaller<?>> marshallersBuilder = ImmutableMap.builder();
    marshallersBuilder.put(STRING, stringMarshaller);
    marshallersBuilder.put(BLOB, blobMarshaller);
    marshallersBuilder.put(FILE, fileMarshaller);
    marshallersBuilder.put(NOTHING, new NothingMarshaller());
    marshallersBuilder.put(STRING_ARRAY, stringArrayMarshaller);
    marshallersBuilder.put(BLOB_ARRAY, blobArrayMarshaller);
    marshallersBuilder.put(FILE_ARRAY, fileArrayMarshaller);
    marshallersBuilder.put(NIL, nilMarshaller);
    this.marshallersMap = marshallersBuilder.build();

    Builder<ArrayType, ArrayMarshaller<?>> arrayMarshallersBuilder = ImmutableMap.builder();
    arrayMarshallersBuilder.put(STRING_ARRAY, stringArrayMarshaller);
    arrayMarshallersBuilder.put(BLOB_ARRAY, blobArrayMarshaller);
    arrayMarshallersBuilder.put(FILE_ARRAY, fileArrayMarshaller);
    arrayMarshallersBuilder.put(NIL, nilMarshaller);
    this.arrayMarshallersMap = arrayMarshallersBuilder.build();
  }

  public StringMarshaller stringMarshaller() {
    return stringMarshaller;
  }

  public BlobMarshaller blobMarshaller() {
    return blobMarshaller;
  }

  public FileMarshaller fileMarshaller() {
    return fileMarshaller;
  }

  public <T extends Value> ArrayMarshaller<T> arrayMarshaller(ArrayType arrayType) {
    ArrayMarshaller<T> reader = (ArrayMarshaller<T>) arrayMarshallersMap.get(arrayType);
    if (reader == null) {
      throw new RuntimeException("Unexpected value type: " + arrayType);
    }
    return reader;
  }

  public <T extends Value> ObjectMarshaller<T> marshaller(Type type) {
    ObjectMarshaller<T> reader = (ObjectMarshaller<T>) marshallersMap.get(type);
    if (reader == null) {
      throw new RuntimeException("Unexpected value type: " + type);
    }
    return reader;
  }
}
