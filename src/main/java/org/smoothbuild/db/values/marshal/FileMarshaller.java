package org.smoothbuild.db.values.marshal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;

import com.google.common.hash.HashCode;

public class FileMarshaller implements ValueMarshaller<SFile> {
  private final HashedDb hashedDb;
  private final StringMarshaller stringMarshaller;
  private final BlobMarshaller blobMarshaller;

  public FileMarshaller(HashedDb hashedDb) {
    this.hashedDb = checkNotNull(hashedDb);
    this.stringMarshaller = new StringMarshaller(hashedDb);
    this.blobMarshaller = new BlobMarshaller(hashedDb);
  }

  public SFile write(SString path, Blob content) {
    Marshaller marshaller = new Marshaller();
    marshaller.write(path.hash());
    marshaller.write(content.hash());
    byte[] bytes = marshaller.getBytes();

    HashCode hash = hashedDb.write(bytes);
    return new SFile(hash, path, content);
  }

  @Override
  public SFile read(HashCode hash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, hash)) {
      SString path = stringMarshaller.read(unmarshaller.readHash());
      Blob blob = blobMarshaller.read(unmarshaller.readHash());
      return new SFile(hash, path, blob);
    }
  }
}
