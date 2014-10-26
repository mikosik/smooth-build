package org.smoothbuild.db.objects.marshal;

import static org.smoothbuild.lang.base.STypes.BLOB;
import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.NOTHING;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.Objects;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SNothing;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;

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
  private final ArrayMarshaller<SNothing> nilMarshaller;

  private final ImmutableMap<SType<?>, ObjectMarshaller<?>> marshallersMap;
  private final ImmutableMap<SArrayType<?>, ArrayMarshaller<?>> arrayMarshallersMap;

  @Inject
  public ObjectMarshallers(@Objects HashedDb hashedDb) {
    this.stringMarshaller = new StringMarshaller(hashedDb);
    this.blobMarshaller = new BlobMarshaller(hashedDb);
    this.fileMarshaller = new FileMarshaller(hashedDb, blobMarshaller);
    this.nothingMarshaller = new NothingMarshaller();

    this.stringArrayMarshaller = new ArrayMarshaller<>(hashedDb, STRING_ARRAY, stringMarshaller);
    this.blobArrayMarshaller = new ArrayMarshaller<>(hashedDb, BLOB_ARRAY, blobMarshaller);
    this.fileArrayMarshaller = new ArrayMarshaller<>(hashedDb, FILE_ARRAY, fileMarshaller);
    this.nilMarshaller = new ArrayMarshaller<>(hashedDb, NIL, nothingMarshaller);

    Builder<SType<?>, ObjectMarshaller<?>> marshallersBuilder = ImmutableMap.builder();
    marshallersBuilder.put(STRING, stringMarshaller);
    marshallersBuilder.put(BLOB, blobMarshaller);
    marshallersBuilder.put(FILE, fileMarshaller);
    marshallersBuilder.put(NOTHING, new NothingMarshaller());
    marshallersBuilder.put(STRING_ARRAY, stringArrayMarshaller);
    marshallersBuilder.put(BLOB_ARRAY, blobArrayMarshaller);
    marshallersBuilder.put(FILE_ARRAY, fileArrayMarshaller);
    marshallersBuilder.put(NIL, nilMarshaller);
    this.marshallersMap = marshallersBuilder.build();

    Builder<SArrayType<?>, ArrayMarshaller<?>> arrayMarshallersBuilder = ImmutableMap.builder();
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

  public <T extends SValue> ArrayMarshaller<T> arrayMarshaller(SArrayType<T> arrayType) {
    /*
     * Cast is safe as readersMap is immutable and constructed in proper way.
     */
    @SuppressWarnings("unchecked")
    ArrayMarshaller<T> reader = (ArrayMarshaller<T>) arrayMarshallersMap.get(arrayType);
    if (reader == null) {
      throw new Message(FATAL, "Unexpected value type: " + arrayType);
    }
    return reader;
  }

  public <T extends SValue> ObjectMarshaller<T> marshaller(SType<T> type) {
    /*
     * Cast is safe as readersMap is immutable and constructed in proper way.
     */
    @SuppressWarnings("unchecked")
    ObjectMarshaller<T> reader = (ObjectMarshaller<T>) marshallersMap.get(type);
    if (reader == null) {
      throw new Message(FATAL, "Unexpected value type: " + type);
    }
    return reader;
  }
}
