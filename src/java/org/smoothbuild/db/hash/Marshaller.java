package org.smoothbuild.db.hash;

import static org.smoothbuild.command.SmoothContants.CHARSET;
import static org.smoothbuild.db.hash.HashedDb.FALSE_AS_BYTE;
import static org.smoothbuild.db.hash.HashedDb.TRUE_AS_BYTE;

import java.util.List;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.Hashed;

import com.google.common.hash.HashCode;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class Marshaller {
  private final ByteArrayDataOutput dataOutput;

  public Marshaller() {
    this.dataOutput = ByteStreams.newDataOutput(256);
  }

  public void write(List<? extends Hashed> elements) {
    List<Hashed> sortedElements = HashedSorter.sort(elements);

    write(sortedElements.size());
    for (Hashed element : sortedElements) {
      write(element.hash());
    }
  }

  public void write(Path path) {
    write(path.value());
  }

  public void write(String string) {
    byte[] pathBytes = string.getBytes(CHARSET);
    dataOutput.writeInt(pathBytes.length);
    dataOutput.write(pathBytes);
  }

  public void write(HashCode hash) {
    dataOutput.write(hash.asBytes());
  }

  public void write(boolean boolValue) {
    dataOutput.writeByte(boolValue ? TRUE_AS_BYTE : FALSE_AS_BYTE);
  }

  public void write(byte byteValue) {
    dataOutput.writeByte(byteValue);
  }

  public void write(int intValue) {
    dataOutput.writeInt(intValue);
  }

  public byte[] getBytes() {
    return dataOutput.toByteArray();
  }
}
