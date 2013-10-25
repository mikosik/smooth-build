package org.smoothbuild.object;

import static org.smoothbuild.object.HashedDb.STRING_CHARSET;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.hash.Hash;
import org.smoothbuild.hash.HashXorer;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.StringValue;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class ObjectDb {
  private final HashedDb hashedDb;

  @Inject
  public ObjectDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  // FileSet

  public FileSetObject fileSet(List<File> elements) {
    HashCode hash = genericSet(elements);
    return new FileSetObject(this, hash);
  }

  public FileSetObject fileSet(HashCode hash) {
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

  public StringSetObject stringSet(List<StringValue> elements) {
    HashCode hash = genericSet(elements);
    return new StringSetObject(this, hash);
  }

  public StringSetObject stringSet(HashCode hash) {
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

  private HashCode genericSet(List<? extends Hashed> elements) {
    Marshaller marshaller = new Marshaller(hashedDb);
    HashXorer hashXorer = new HashXorer();

    marshaller.addInt(elements.size());
    for (Hashed hashed : elements) {
      HashCode hash = hashed.hash();
      marshaller.addHash(hash);
      hashXorer.xorWith(hash);
    }

    /*
     * Xored hashes result has to be hashed once again so in case of single
     * element list its hash is different from its only element hash.
     */
    HashCode hash = Hash.bytes(hashXorer.hash().asBytes());

    return marshaller.store(hash);
  }

  // File

  public FileObject file(Path path, byte[] bytes) {
    BlobObject blob = blob(bytes);
    HashCode contentHash = blob.hash();

    Marshaller marshaller = new Marshaller(hashedDb);
    marshaller.addHash(contentHash);
    marshaller.addPath(path);
    HashCode hash = marshaller.store();

    return new FileObject(path, blob, hash);
  }

  public FileObject file(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      HashCode blobHash = unmarshaller.readHash();
      Path path = unmarshaller.readPath();
      BlobObject blob = blob(blobHash);

      return new FileObject(path, blob, hash);
    }
  }

  // String

  public StringObject string(String string) {
    HashCode hash = hashedDb.store(string.getBytes(STRING_CHARSET));
    return new StringObject(hashedDb, hash);
  }

  public StringObject string(HashCode hash) {
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
