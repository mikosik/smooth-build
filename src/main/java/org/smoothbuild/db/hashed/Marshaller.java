package org.smoothbuild.db.hashed;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Marshaller {
  private final ByteArrayDataOutput dataOutput;

  public Marshaller() {
    this.dataOutput = ByteStreams.newDataOutput(256);
  }

  public void write(HashCode hash) {
    dataOutput.write(hash.asBytes());
  }

  public void write(int intValue) {
    dataOutput.writeInt(intValue);
  }

  public byte[] getBytes() {
    return dataOutput.toByteArray();
  }
}
