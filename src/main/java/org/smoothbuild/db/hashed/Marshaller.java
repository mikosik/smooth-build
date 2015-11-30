package org.smoothbuild.db.hashed;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Marshaller {
  private final HashedDb hashedDb;
  private final ByteArrayDataOutput dataOutput;

  public Marshaller(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
    this.dataOutput = ByteStreams.newDataOutput(256);
  }

  public void write(HashCode hash) {
    dataOutput.write(hash.asBytes());
  }

  public void write(int intValue) {
    dataOutput.writeInt(intValue);
  }

  public HashCode close() {
    return hashedDb.write(dataOutput.toByteArray());
  }

  public HashCode close(HashCode taskHash) {
    return hashedDb.write(taskHash, dataOutput.toByteArray());
  }
}
