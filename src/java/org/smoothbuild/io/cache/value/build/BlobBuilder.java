package org.smoothbuild.io.cache.value.build;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.base.SBlob;

public class BlobBuilder {
  private final ValueDb valueDb;

  private ByteArrayOutputStream outputStream;

  public BlobBuilder(ValueDb valueDb) {
    this.valueDb = valueDb;
  }

  public OutputStream openOutputStream() {
    checkState(this.outputStream == null, "Cannot open output stream twice.");
    this.outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  public SBlob build() {
    checkState(outputStream != null, "No content available. Create one via openOutputStream()");
    return valueDb.writeBlob(outputStream.toByteArray());
  }
}
