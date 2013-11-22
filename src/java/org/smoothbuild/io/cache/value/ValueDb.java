package org.smoothbuild.io.cache.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.type.Type.BLOB_SET;
import static org.smoothbuild.lang.type.Type.FILE_SET;
import static org.smoothbuild.lang.type.Type.STRING_SET;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.io.cache.hash.ValuesCache;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Hashed;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ValueDb {
  private final HashedDb hashedDb;

  @Inject
  public ValueDb(@ValuesCache HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  // FileArray

  public ArrayBuilder<File> fileArrayBuilder() {
    return new ArrayBuilder<File>(this, Type.FILE_SET, fileReader());
  }

  public Array<File> fileArray(HashCode hash) {
    return new CachedArray<File>(this, hash, FILE_SET, fileReader());
  }

  // BlobArray

  public ArrayBuilder<Blob> blobArrayBuilder() {
    return new ArrayBuilder<Blob>(this, Type.BLOB_SET, blobReader());
  }

  public Array<Blob> blobArray(HashCode hash) {
    return new CachedArray<Blob>(this, hash, BLOB_SET, blobReader());
  }

  // StringArray

  public ArrayBuilder<StringValue> stringArrayBuilder() {
    return new ArrayBuilder<StringValue>(this, Type.STRING_SET, stringReader());
  }

  public Array<StringValue> stringArray(HashCode hash) {
    return new CachedArray<StringValue>(this, hash, STRING_SET, stringReader());
  }

  // generic array

  public <T extends Value> Iterable<T> array(HashCode hash, ValueReader<T> valueReader) {
    ImmutableList.Builder<T> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash)) {
      builder.add(valueReader.read(elemHash));
    }
    return builder.build();
  }

  private List<HashCode> readHashCodeList(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      return unmarshaller.readHashCodeList();
    }
  }

  public <T extends Value> Array<T> array(List<T> elements, Type type, ValueReader<T> valueReader) {
    HashCode hash = genericArray(elements);
    return new CachedArray<T>(this, hash, type, valueReader);
  }

  private HashCode genericArray(List<? extends Hashed> elements) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements);
    return hashedDb.store(marshaller.getBytes());
  }

  // File

  public File file(Path path, byte[] bytes) {
    CachedBlob blob = blob(bytes);
    HashCode contentHash = blob.hash();

    Marshaller marshaller = new Marshaller();
    marshaller.write(contentHash);
    marshaller.write(path);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    return new CachedFile(path, blob, hash);
  }

  protected ValueReader<File> fileReader() {
    return new ValueReader<File>() {
      @Override
      public File read(HashCode hash) {
        return file(hash);
      }
    };
  }

  public File file(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      HashCode blobHash = unmarshaller.readHash();
      Path path = unmarshaller.readPath();
      CachedBlob blob = blob(blobHash);

      return new CachedFile(path, blob, hash);
    }
  }

  // String

  public StringValue string(String string) {
    HashCode hash = hashedDb.store(string.getBytes(CHARSET));
    return new CachedString(hashedDb, hash);
  }

  private ValueReader<StringValue> stringReader() {
    return new ValueReader<StringValue>() {
      @Override
      public StringValue read(HashCode hash) {
        return string(hash);
      }
    };
  }

  public StringValue string(HashCode hash) {
    return new CachedString(hashedDb, hash);
  }

  // Blob

  public CachedBlob blob(byte[] objectBytes) {
    HashCode hash = hashedDb.store(objectBytes);
    return new CachedBlob(hashedDb, hash);
  }

  private ValueReader<Blob> blobReader() {
    return new ValueReader<Blob>() {
      @Override
      public Blob read(HashCode hash) {
        return blob(hash);
      }
    };
  }

  public CachedBlob blob(HashCode hash) {
    return new CachedBlob(hashedDb, hash);
  }
}
