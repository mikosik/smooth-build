package org.smoothbuild.io.cache.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.lang.type.Type.BLOB_ARRAY;
import static org.smoothbuild.lang.type.Type.FILE_ARRAY;
import static org.smoothbuild.lang.type.Type.STRING_ARRAY;

import java.util.List;

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
import org.smoothbuild.lang.type.Hashed;
import org.smoothbuild.lang.type.SString;
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

  public ArrayBuilder<SFile> fileArrayBuilder() {
    return new ArrayBuilder<SFile>(this, Type.FILE_ARRAY, fileReader());
  }

  public SArray<SFile> fileArray(HashCode hash) {
    return new CachedArray<SFile>(this, hash, FILE_ARRAY, fileReader());
  }

  // BlobArray

  public ArrayBuilder<SBlob> blobArrayBuilder() {
    return new ArrayBuilder<SBlob>(this, Type.BLOB_ARRAY, blobReader());
  }

  public SArray<SBlob> blobArray(HashCode hash) {
    return new CachedArray<SBlob>(this, hash, BLOB_ARRAY, blobReader());
  }

  // StringArray

  public ArrayBuilder<SString> stringArrayBuilder() {
    return new ArrayBuilder<SString>(this, Type.STRING_ARRAY, stringReader());
  }

  public SArray<SString> stringArray(HashCode hash) {
    return new CachedArray<SString>(this, hash, STRING_ARRAY, stringReader());
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

  public <T extends Value> SArray<T> array(List<T> elements, Type type, ValueReader<T> valueReader) {
    HashCode hash = genericArray(elements);
    return new CachedArray<T>(this, hash, type, valueReader);
  }

  private HashCode genericArray(List<? extends Hashed> elements) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements);
    return hashedDb.store(marshaller.getBytes());
  }

  // File

  public SFile file(Path path, byte[] bytes) {
    CachedBlob blob = blob(bytes);
    HashCode contentHash = blob.hash();

    Marshaller marshaller = new Marshaller();
    marshaller.write(contentHash);
    marshaller.write(path);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    return new CachedFile(path, blob, hash);
  }

  protected ValueReader<SFile> fileReader() {
    return new ValueReader<SFile>() {
      @Override
      public SFile read(HashCode hash) {
        return file(hash);
      }
    };
  }

  public SFile file(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      HashCode blobHash = unmarshaller.readHash();
      Path path = unmarshaller.readPath();
      CachedBlob blob = blob(blobHash);

      return new CachedFile(path, blob, hash);
    }
  }

  // String

  public SString string(String string) {
    HashCode hash = hashedDb.store(string.getBytes(CHARSET));
    return new CachedString(hashedDb, hash);
  }

  private ValueReader<SString> stringReader() {
    return new ValueReader<SString>() {
      @Override
      public SString read(HashCode hash) {
        return string(hash);
      }
    };
  }

  public SString string(HashCode hash) {
    return new CachedString(hashedDb, hash);
  }

  // Blob

  public CachedBlob blob(byte[] objectBytes) {
    HashCode hash = hashedDb.store(objectBytes);
    return new CachedBlob(hashedDb, hash);
  }

  private ValueReader<SBlob> blobReader() {
    return new ValueReader<SBlob>() {
      @Override
      public SBlob read(HashCode hash) {
        return blob(hash);
      }
    };
  }

  public CachedBlob blob(HashCode hash) {
    return new CachedBlob(hashedDb, hash);
  }
}
