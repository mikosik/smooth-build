package org.smoothbuild.db.objects.build;

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.SBlob;

public class BlobBuilder {
  private final ObjectsDb objectsDb;

  private ByteArrayOutputStream outputStream;

  public BlobBuilder(ObjectsDb objectsDb) {
    this.objectsDb = objectsDb;
  }

  public OutputStream openOutputStream() {
    checkState(this.outputStream == null, "Cannot open output stream twice.");
    this.outputStream = new ByteArrayOutputStream();
    return outputStream;
  }

  public SBlob build() {
    checkState(outputStream != null, "No content available. Create one via openOutputStream()");
    return objectsDb.writeBlob(outputStream.toByteArray());
  }
}
