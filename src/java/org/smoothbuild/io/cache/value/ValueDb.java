package org.smoothbuild.io.cache.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.io.cache.hash.ValuesCache;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.lang.type.Hashed;
import org.smoothbuild.lang.type.StringValue;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ValueDb {
  private final HashedDb hashedDb;

  @Inject
  public ValueDb(@ValuesCache HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  // FileSet

  public Array<File> fileSet(List<File> elements) {
    HashCode hash = genericSet(elements);
    return new CachedFileSet(this, hash);
  }

  public Array<File> fileSet(HashCode hash) {
    return new CachedFileSet(this, hash);
  }

  public Iterable<File> fileSetIterable(HashCode hash) {
    ImmutableList.Builder<File> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash)) {
      builder.add(file(elemHash));
    }
    return builder.build();
  }

  // BlobSet

  public Array<Blob> blobSet(List<Blob> elements) {
    HashCode hash = genericSet(elements);
    return new CachedBlobSet(this, hash);
  }

  public Array<Blob> blobSet(HashCode hash) {
    return new CachedBlobSet(this, hash);
  }

  public Iterable<Blob> blobSetIterable(HashCode hash) {
    ImmutableList.Builder<Blob> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash)) {
      builder.add(blob(elemHash));
    }
    return builder.build();
  }

  // StringSet

  public Array<StringValue> stringSet(List<StringValue> elements) {
    HashCode hash = genericSet(elements);
    return new CachedStringSet(this, hash);
  }

  public Array<StringValue> stringSet(HashCode hash) {
    return new CachedStringSet(this, hash);
  }

  public Iterable<StringValue> stringSetIterable(HashCode hash) {
    ImmutableList.Builder<StringValue> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash)) {
      builder.add(string(elemHash));
    }
    return builder.build();
  }

  // generic set

  private HashCode genericSet(List<? extends Hashed> elements) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(elements);
    return hashedDb.store(marshaller.getBytes());
  }

  private List<HashCode> readHashCodeList(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      return unmarshaller.readHashCodeList();
    }
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

  public StringValue string(HashCode hash) {
    return new CachedString(hashedDb, hash);
  }

  // Blob

  public CachedBlob blob(byte[] objectBytes) {
    HashCode hash = hashedDb.store(objectBytes);
    return new CachedBlob(hashedDb, hash);
  }

  public CachedBlob blob(HashCode hash) {
    return new CachedBlob(hashedDb, hash);
  }
}
