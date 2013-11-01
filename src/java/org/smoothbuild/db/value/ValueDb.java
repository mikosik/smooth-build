package org.smoothbuild.db.value;

import static org.smoothbuild.db.hash.HashedDb.STRING_CHARSET;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.db.hash.HashedDbWithValues;
import org.smoothbuild.db.hash.Marshaller;
import org.smoothbuild.db.hash.Unmarshaller;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;

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
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      int size = unmarshaller.readInt();
      for (int i = 0; i < size; i++) {
        builder.add(file(unmarshaller.readHash()));
      }
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
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      int size = unmarshaller.readInt();
      for (int i = 0; i < size; i++) {
        builder.add(string(unmarshaller.readHash()));
      }
    }
    return builder.build();
  }

  // generic set

  private HashCode genericSet(List<? extends Value> elements) {
    Marshaller marshaller = new Marshaller();
    List<Value> sortedElements = HashedSorter.sort(elements);

    marshaller.addInt(sortedElements.size());
    for (Value value : sortedElements) {
      marshaller.addHash(value.hash());
    }

    return hashedDb.store(marshaller.getBytes());
  }

  // File

  public File file(Path path, byte[] bytes) {
    BlobObject blob = blob(bytes);
    HashCode contentHash = blob.hash();

    Marshaller marshaller = new Marshaller();
    marshaller.addHash(contentHash);
    marshaller.addPath(path);
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
    HashCode hash = hashedDb.store(string.getBytes(STRING_CHARSET));
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
