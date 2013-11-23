package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.type.Type.BLOB;
import static org.smoothbuild.lang.type.Type.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Type.FILE;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.STRING;
import static org.smoothbuild.lang.type.Type.STRING_ARRAY;
import static org.smoothbuild.message.base.MessageType.FATAL;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.io.cache.hash.ValuesCache;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.type.SArray;
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
  private final StringReader stringReader = new StringReader();
  private final BlobReader blobReader = new BlobReader();
  private final FileReader fileReader = new FileReader();
  private final ArrayReader<SString> stringArrayReader = new ArrayReader<SString>(STRING_ARRAY,
      stringReader);
  private final ArrayReader<SBlob> blobArrayReader = new ArrayReader<SBlob>(BLOB_ARRAY, blobReader);
  private final ArrayReader<SFile> fileArrayReader = new ArrayReader<SFile>(FILE_ARRAY, fileReader);
  private final ImmutableMap<Type<?>, ValueReader<?>> readersMap;

  @Inject
  public ValueDb(@ValuesCache HashedDb hashedDb) {
    this.hashedDb = hashedDb;

    Builder<Type<?>, ValueReader<?>> builder = ImmutableMap.builder();
    builder.put(STRING, stringReader);
    builder.put(BLOB, blobReader);
    builder.put(FILE, fileReader);
    builder.put(STRING_ARRAY, stringArrayReader);
    builder.put(BLOB_ARRAY, blobArrayReader);
    builder.put(FILE_ARRAY, fileArrayReader);

    this.readersMap = builder.build();
  }

  public ArrayBuilder<SFile> fileArrayBuilder() {
    return new ArrayBuilder<SFile>(hashedDb, FILE_ARRAY, fileReader);
  }

  public ArrayBuilder<SBlob> blobArrayBuilder() {
    return new ArrayBuilder<SBlob>(hashedDb, BLOB_ARRAY, blobReader);
  }

  public ArrayBuilder<SString> stringArrayBuilder() {
    return new ArrayBuilder<SString>(hashedDb, STRING_ARRAY, stringReader);
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
    ValueReader<T> reader = (ValueReader<T>) readersMap.get(typeLiteral);
    if (reader == null) {
      throw new ErrorMessageException(new Message(FATAL,
          "Bug in smooth binary: Unexpected value type " + typeLiteral));
    }
    return reader.read(hash);
  }

  private final class FileReader implements ValueReader<SFile> {
    @Override
    public SFile read(HashCode hash) {
      try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
        HashCode blobHash = unmarshaller.readHash();
        Path path = unmarshaller.readPath();
        // TODO copy pasted from BlobReader
        CachedBlob blob = new CachedBlob(hashedDb, blobHash);

        return new CachedFile(path, blob, hash);
      }
    }
  }

  private class StringReader implements ValueReader<SString> {
    @Override
    public SString read(HashCode hash) {
      return new CachedString(hashedDb, hash);
    }
  }

  private class BlobReader implements ValueReader<SBlob> {
    @Override
    public SBlob read(HashCode hash) {
      return new CachedBlob(hashedDb, hash);
    }
  }

  private class ArrayReader<T extends Value> implements ValueReader<SArray<T>> {
    private final ValueReader<T> valueReader;
    private final Type<?> arrayType;

    public ArrayReader(Type<?> arrayType, ValueReader<T> valueReader) {
      this.arrayType = checkNotNull(arrayType);
      this.valueReader = checkNotNull(valueReader);
    }

    @Override
    public SArray<T> read(HashCode hash) {
      return new CachedArray<T>(hashedDb, hash, arrayType, valueReader);
    }
  }
}
