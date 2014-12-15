package org.smoothbuild.db.objects.marshal;

import static org.smoothbuild.lang.base.Types.BLOB;
import static org.smoothbuild.lang.base.Types.BLOB_ARRAY;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.FILE_ARRAY;
import static org.smoothbuild.lang.base.Types.NIL;
import static org.smoothbuild.lang.base.Types.NOTHING;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.base.Types.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.objects.Objects;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
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
  private final ArrayMarshaller<Nothing> nilMarshaller;

  private final ImmutableMap<Type, ObjectMarshaller<?>> marshallersMap;
  private final ImmutableMap<ArrayType, ArrayMarshaller<?>> arrayMarshallersMap;

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
      throw new Message(FATAL, "Unexpected value type: " + arrayType);
    }
    return reader;
  }

  public <T extends Value> ObjectMarshaller<T> marshaller(Type type) {
    ObjectMarshaller<T> reader = (ObjectMarshaller<T>) marshallersMap.get(type);
    if (reader == null) {
      throw new Message(FATAL, "Unexpected value type: " + type);
    }
    return reader;
  }
}
