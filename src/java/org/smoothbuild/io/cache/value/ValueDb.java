package org.smoothbuild.io.cache.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.type.STypes.BLOB;
import static org.smoothbuild.lang.type.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.ValuesCache;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;
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

  private final ImmutableMap<Type<?>, ReadValue<?>> readersMap;

  @Inject
  public ValueDb(@ValuesCache HashedDb hashedDb) {
    this.hashedDb = hashedDb;

    this.readString = new ReadString(hashedDb);
    this.readBlob = new ReadBlob(hashedDb);
    this.readFile = new ReadFile(hashedDb);
    this.readStringArray = new ReadArray<SString>(hashedDb, STRING_ARRAY, readString);
    this.readBlobArray = new ReadArray<SBlob>(hashedDb, BLOB_ARRAY, readBlob);
    this.readFileArray = new ReadArray<SFile>(hashedDb, FILE_ARRAY, readFile);

    Builder<Type<?>, ReadValue<?>> builder = ImmutableMap.builder();
    builder.put(STRING, readString);
    builder.put(BLOB, readBlob);
    builder.put(FILE, readFile);
    builder.put(STRING_ARRAY, readStringArray);
    builder.put(BLOB_ARRAY, readBlobArray);
    builder.put(FILE_ARRAY, readFileArray);

    this.readersMap = builder.build();
  }

  public ArrayBuilder<SFile> fileArrayBuilder() {
    return new ArrayBuilder<SFile>(hashedDb, FILE_ARRAY, readFile);
  }

  public ArrayBuilder<SBlob> blobArrayBuilder() {
    return new ArrayBuilder<SBlob>(hashedDb, BLOB_ARRAY, readBlob);
  }

  public ArrayBuilder<SString> stringArrayBuilder() {
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

  public <T extends Value> T read(Type<T> typeLiteral, HashCode hash) {
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
