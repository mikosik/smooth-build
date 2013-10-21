package org.smoothbuild.object;

import static org.smoothbuild.object.HashedDb.STRING_CHARSET;

import org.smoothbuild.fs.base.Path;

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

  public void addPath(Path path) {
    byte[] pathBytes = path.value().getBytes(STRING_CHARSET);
    dataOutput.writeInt(pathBytes.length);
    dataOutput.write(pathBytes);
  }

  public void addHash(HashCode hash) {
    dataOutput.write(hash.asBytes());
  }

  public void addInt(int intValue) {
    dataOutput.writeInt(intValue);
  }

  public HashCode store() {
    return hashedDb.store(dataOutput.toByteArray());
  }

  public HashCode store(HashCode hash) {
    return hashedDb.store(hash, dataOutput.toByteArray());
  }
}
