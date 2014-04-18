package org.smoothbuild.db.objects.build;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.db.objects.marshal.BlobMarshaller;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SBlob;

public class BlobBuilderImpl implements BlobBuilder {
  private final BlobMarshaller marshaller;
  private ByteArrayOutputStream outputStream;

  public BlobBuilderImpl(BlobMarshaller marshaller) {
    this.marshaller = marshaller;
  }

  @Override
  public OutputStream openOutputStream() {
    checkState(this.outputStream == null, "Cannot open output stream twice.");
    this.outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  @Override
  public SBlob build() {
    checkState(outputStream != null, "No content available. Create one via openOutputStream()");
    byte[] bytes = outputStream.toByteArray();
    return marshaller.write(bytes);
  }
}
