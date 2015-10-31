package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.db.values.marshal.BlobMarshaller;

public class BlobBuilder {
  private final BlobMarshaller marshaller;
  private ByteArrayOutputStream outputStream;
  private boolean closed;

  public BlobBuilder(BlobMarshaller marshaller) {
    this.marshaller = marshaller;
    this.closed = false;
  }

  public OutputStream openOutputStream() {
    checkState(!closed, "Cannot open output stream as close() has been already called.");
    checkState(outputStream == null, "Cannot open output stream twice.");
    outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  public Blob build() {
    closed = true;
    return marshaller.write(getBytes());
  }

  private byte[] getBytes() {
    if (outputStream == null) {
      return new byte[] {};
    } else {
      return outputStream.toByteArray();
    }
  }
}
