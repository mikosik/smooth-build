package org.smoothbuild.db.value;

import static org.smoothbuild.command.SmoothContants.CHARSET;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.db.hash.HashedDbWithValues;
import org.smoothbuild.db.hash.Marshaller;
import org.smoothbuild.db.hash.Unmarshaller;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.Hashed;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ValueDb {
  private final HashedDb hashedDb;

  @Inject
  public ValueDb(@HashedDbWithValues HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  // FileSet

  public FileSet fileSet(List<File> elements) {
    HashCode hash = genericSet(elements);
    return new FileSetObject(this, hash);
  }

  public FileSet fileSet(HashCode hash) {
    return new FileSetObject(this, hash);
  }

  public Iterable<File> fileSetIterable(HashCode hash) {
    ImmutableList.Builder<File> builder = ImmutableList.builder();
    for (HashCode elemHash : readHashCodeList(hash)) {
      builder.add(file(elemHash));
    }
    return builder.build();
  }

  // StringSet

  public StringSet stringSet(List<StringValue> elements) {
    HashCode hash = genericSet(elements);
    return new StringSetObject(this, hash);
  }

  public StringSet stringSet(HashCode hash) {
    return new StringSetObject(this, hash);
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
    BlobObject blob = blob(bytes);
    HashCode contentHash = blob.hash();

    Marshaller marshaller = new Marshaller();
    marshaller.write(contentHash);
    marshaller.write(path);
    HashCode hash = hashedDb.store(marshaller.getBytes());

    return new FileObject(path, blob, hash);
  }

  public File file(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      HashCode blobHash = unmarshaller.readHash();
      Path path = unmarshaller.readPath();
      BlobObject blob = blob(blobHash);

      return new FileObject(path, blob, hash);
    }
  }

  // String

  public StringValue string(String string) {
    HashCode hash = hashedDb.store(string.getBytes(CHARSET));
    return new StringObject(hashedDb, hash);
  }

  public StringValue string(HashCode hash) {
    return new StringObject(hashedDb, hash);
  }

  // Blob

  public BlobObject blob(byte[] objectBytes) {
    HashCode hash = hashedDb.store(objectBytes);
    return new BlobObject(hashedDb, hash);
  }

  public BlobObject blob(HashCode hash) {
    return new BlobObject(hashedDb, hash);
  }
}
