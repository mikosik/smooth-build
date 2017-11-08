package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.Types.BLOB;

import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;

public class Blob extends Value {
  private final HashedDb hashedDB;

  public Blob(HashCode hash, HashedDb hashedDb) {
    super(BLOB, hash);
    this.hashedDB = checkNotNull(hashedDb);
  }

  public InputStream openInputStream() {
    return hashedDB.newUnmarshaller(hash());
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
