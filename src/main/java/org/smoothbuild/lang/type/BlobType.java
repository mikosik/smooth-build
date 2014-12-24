package org.smoothbuild.lang.type;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.Value;

public class BlobType extends Type {
  protected BlobType() {
    super("Blob", Blob.class);
  }

  @Override
  public Value defaultValue(ObjectsDb objectsDb) {
    BlobBuilder blobBuilder = objectsDb.blobBuilder();
    blobBuilder.openOutputStream();
    return blobBuilder.build();
  }
}
