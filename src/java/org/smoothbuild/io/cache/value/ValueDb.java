package org.smoothbuild.io.cache.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.ValuesCache;
import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.instance.CachedBlob;
import org.smoothbuild.io.cache.value.instance.CachedFile;
import org.smoothbuild.io.cache.value.instance.CachedString;
import org.smoothbuild.io.cache.value.read.ReadArray;
import org.smoothbuild.io.cache.value.read.ReadBlob;
import org.smoothbuild.io.cache.value.read.ReadFile;
import org.smoothbuild.io.cache.value.read.ReadNothing;
import org.smoothbuild.io.cache.value.read.ReadString;
import org.smoothbuild.io.cache.value.read.ReadValue;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SArrayType;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SNothing;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.hash.HashCode;

public class ValueDb {
  private final HashedDb hashedDb;

  private final ReadString readString;
  private final ReadBlob readBlob;
  private final ReadFile readFile;
  private final ReadArray<SString> readStringArray;
  private final ReadArray<SBlob> readBlobArray;
  private final ReadArray<SFile> readFileArray;
  private final ReadNothing readNothing;

  private final ImmutableMap<SType<?>, ReadValue<?>> readersMap;

  @Inject
  public ValueDb(@ValuesCache HashedDb hashedDb) {
    this.hashedDb = hashedDb;

    this.readString = new ReadString(hashedDb);
    this.readBlob = new ReadBlob(hashedDb);
    this.readFile = new ReadFile(hashedDb);
    this.readStringArray = new ReadArray<SString>(hashedDb, STRING_ARRAY, readString);
    this.readBlobArray = new ReadArray<SBlob>(hashedDb, BLOB_ARRAY, readBlob);
    this.readFileArray = new ReadArray<SFile>(hashedDb, FILE_ARRAY, readFile);
    this.readNothing = new ReadNothing();

    Builder<SType<?>, ReadValue<?>> builder = ImmutableMap.builder();
    builder.put(STRING, readString);
    builder.put(BLOB, readBlob);
    builder.put(FILE, readFile);
    builder.put(STRING_ARRAY, readStringArray);
    builder.put(BLOB_ARRAY, readBlobArray);
    builder.put(FILE_ARRAY, readFileArray);

    this.readersMap = builder.build();
  }

  // array builders

  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    if (arrayType == FILE_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result = (ArrayBuilder<T>) fileArrayBuilder();
      return result;
    }
    if (arrayType == BLOB_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result = (ArrayBuilder<T>) blobArrayBuilder();
      return result;
    }
    if (arrayType == STRING_ARRAY) {
      @SuppressWarnings("unchecked")
      ArrayBuilder<T> result = (ArrayBuilder<T>) stringArrayBuilder();
      return result;
    }
    throw new IllegalArgumentException("Cannot create ArrayBuilder for array type = " + arrayType);
  }

  public SArray<SNothing> emptyArray() {
    return new ArrayBuilder<SNothing>(hashedDb, EMPTY_ARRAY, readNothing).build();
  }

  private ArrayBuilder<SFile> fileArrayBuilder() {
    return new ArrayBuilder<SFile>(hashedDb, FILE_ARRAY, readFile);
  }

  private ArrayBuilder<SBlob> blobArrayBuilder() {
    return new ArrayBuilder<SBlob>(hashedDb, BLOB_ARRAY, readBlob);
  }

  private ArrayBuilder<SString> stringArrayBuilder() {
    return new ArrayBuilder<SString>(hashedDb, STRING_ARRAY, readString);
  }

  // writers

  public SFile writeFile(Path path, byte[] bytes) {
    CachedBlob blob = writeBlob(bytes);
    HashCode contentHash = blob.hash();

    Marshaller marshaller = new Marshaller();
    marshaller.write(contentHash);
    marshaller.write(path);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    return new CachedFile(path, blob, hash);
  }

  public SString writeString(String string) {
    HashCode hash = hashedDb.store(string.getBytes(CHARSET));
    return new CachedString(hashedDb, hash);
  }

  public CachedBlob writeBlob(byte[] objectBytes) {
    HashCode hash = hashedDb.store(objectBytes);
    return new CachedBlob(hashedDb, hash);
  }

  // readers

  public <T extends SValue> T read(SType<T> typeLiteral, HashCode hash) {
    /*
     * Cast is safe as readersMap is immutable and constructed in proper way.
     */
    @SuppressWarnings("unchecked")
    ReadValue<T> reader = (ReadValue<T>) readersMap.get(typeLiteral);
    if (reader == null) {
      throw new ErrorMessageException(new Message(FATAL,
          "Bug in smooth binary: Unexpected value type " + typeLiteral));
    }
    return reader.read(hash);
  }
}
