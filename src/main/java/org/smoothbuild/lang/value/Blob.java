package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.util.Streams;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteStreams;
import com.google.common.io.CountingInputStream;

public class Blob extends Value {
  public Blob(HashCode dataHash, Type type, HashedDb hashedDb) {
    super(dataHash, type, hashedDb);
    checkArgument(type.name().equals("Blob"));
  }

  public InputStream openInputStream() {
    return hashedDb.newUnmarshaller(dataHash());
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
