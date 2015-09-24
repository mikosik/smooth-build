package org.smoothbuild.lang.type;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Value;

public class BlobType extends Type {
  protected BlobType() {
    super("Blob", Blob.class);
  }

  @Override
  public Value defaultValue(ObjectsDb objectsDb) {
    return objectsDb.blobBuilder().build();
  }

  @Override
  public boolean isAllowedAsResult() {
    return true;
  }

  @Override
  public boolean isAllowedAsParameter() {
    return true;
  }
}
