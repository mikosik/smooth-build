package org.smoothbuild.db.objects.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.BLOB;

import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;

public class BlobObject extends AbstractObject implements SBlob {
  private final HashedDb hashedDb;

  public BlobObject(HashedDb hashedDb, HashCode hash) {
    super(BLOB, hash);
    this.hashedDb = checkNotNull(hashedDb);
  }

  @Override
  public InputStream openInputStream() {
    return hashedDb.openInputStream(hash());
  }

  @Override
  public String toString() {
    return "Blob(" + size() + " bytes)";
  }

  private long size() {
    try (CountingInputStream inputStream = new CountingInputStream(openInputStream())) {
      Streams.copy(inputStream, ByteStreams.nullOutputStream());
      return inputStream.getCount();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
