package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.db.objects.marshal.BlobMarshaller;

public class BlobBuilder {
  private final BlobMarshaller marshaller;
  private ByteArrayOutputStream outputStream;

  public BlobBuilder(BlobMarshaller marshaller) {
    this.marshaller = marshaller;
  }

  public OutputStream openOutputStream() {
    checkState(this.outputStream == null, "Cannot open output stream twice.");
    this.outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  public Blob build() {
    checkState(outputStream != null, "No content available. Create one via openOutputStream()");
    byte[] bytes = outputStream.toByteArray();
    return marshaller.write(bytes);
  }
}
