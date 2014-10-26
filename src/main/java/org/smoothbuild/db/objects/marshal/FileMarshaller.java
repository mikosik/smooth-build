package org.smoothbuild.db.objects.marshal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.objects.base.FileObject;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;

import com.google.common.hash.HashCode;

public class FileMarshaller implements ObjectMarshaller<SFile> {
  private final HashedDb hashedDb;
  private final BlobMarshaller blobMarshaller;

  public FileMarshaller(HashedDb hashedDb, BlobMarshaller blobMarshaller) {
    this.hashedDb = checkNotNull(hashedDb);
    this.blobMarshaller = checkNotNull(blobMarshaller);
  }

  public SFile write(Path path, Blob content) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(content.hash());
    marshaller.write(path);
    byte[] bytes = marshaller.getBytes();

    HashCode hash = hashedDb.write(bytes);
    return new FileObject(hash, path, content);
  }

  @Override
  public SFile read(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash)) {
      HashCode blobHash = unmarshaller.readHash();
      Path path = unmarshaller.readPath();
      Blob blob = blobMarshaller.read(blobHash);

      return new FileObject(hash, path, blob);
    }
  }
}
