package org.smoothbuild.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.OutputStream;

public class ForwardingOutputStream extends OutputStream {
  private final OutputStream outputStream;

  public ForwardingOutputStream(OutputStream outputStream) {
    this.outputStream = checkNotNull(outputStream);
  }

  public void write(int oneByte) throws IOException {
    outputStream.write(oneByte);
  }

  public void write(byte bytes[]) throws IOException {
    outputStream.write(bytes);
  }

  public void write(byte bytes[], int offset, int length) throws IOException {
    outputStream.write(bytes, offset, length);
  }

  public void flush() throws IOException {
    outputStream.flush();
  }

  public void close() throws IOException {
    outputStream.close();
  }
}
