package org.smoothbuild.object;

import javax.inject.Inject;

import org.smoothbuild.fs.base.Path;

import com.google.common.hash.HashCode;

public class ObjectsDb {
  private final HashedDb hashedDb;

  @Inject
  public ObjectsDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public FileObject file(Path path, byte[] bytes) {
    BlobObject blob = blob(bytes);

    Marshaller marshaller = new Marshaller(hashedDb);
    marshaller.addHash(blob.hash());
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

  public BlobObject blob(byte[] objectBytes) {
    HashCode hash = hashedDb.store(objectBytes);
    return new BlobObject(hashedDb, hash);
  }

  public BlobObject blob(HashCode hash) {
    return new BlobObject(hashedDb, hash);
  }
}
