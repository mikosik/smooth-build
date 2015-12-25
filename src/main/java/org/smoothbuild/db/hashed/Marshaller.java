package org.smoothbuild.db.hashed;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Marshaller extends OutputStream {
  private final HashedDb hashedDb;
  private final ByteArrayDataOutput dataOutput;
  private final HashCode hash;

  public Marshaller(HashedDb hashedDb) {
    this(hashedDb, null);
  }

  public Marshaller(HashedDb hashedDb, HashCode hash) {
    this.hashedDb = hashedDb;
    this.dataOutput = ByteStreams.newDataOutput(256);
    this.hash = hash;
  }

  public void writeHash(HashCode hash) {
    dataOutput.write(hash.asBytes());
  }

  public void writeInt(int intValue) {
    dataOutput.writeInt(intValue);
  }

  @Override
  public void write(int b) throws IOException {
    dataOutput.write(b);
  }

  @Override
  public void write(byte b[]) throws IOException {
    dataOutput.write(b, 0, b.length);
  }

  @Override
  public void write(byte b[], int off, int len) {
    dataOutput.write(b, off, len);
  }

  public HashCode closeMarshaller() {
    if (hash == null) {
      return hashedDb.write(dataOutput.toByteArray());
    } else {
      return hashedDb.write(hash, dataOutput.toByteArray());
    }
  }
}
