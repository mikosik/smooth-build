package org.smoothbuild.io.cache.value;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.io.cache.value.instance.CachedBlob;
import org.smoothbuild.io.cache.value.instance.CachedFile;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SFile;

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
      // TODO copy pasted from BlobReader
      CachedBlob blob = new CachedBlob(hashedDb, blobHash);

      return new CachedFile(path, blob, hash);
    }
  }
}