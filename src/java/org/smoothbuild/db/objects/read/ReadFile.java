package org.smoothbuild.db.objects.read;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.objects.instance.BlobObject;
import org.smoothbuild.db.objects.instance.FileObject;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SFile;

import com.google.common.hash.HashCode;

public class ReadFile implements ReadValue<SFile> {
  private final HashedDb hashedDb;

  public ReadFile(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  @Override
  public SFile read(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash);) {
      HashCode blobHash = unmarshaller.readHash();
      Path path = unmarshaller.readPath();
      BlobObject blob = new BlobObject(hashedDb, blobHash);

      return new FileObject(path, blob, hash);
    }
  }
}
