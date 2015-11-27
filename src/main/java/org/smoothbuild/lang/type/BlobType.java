package org.smoothbuild.lang.type;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.value.Blob;

public class BlobType extends Type {
  protected BlobType() {
    super("Blob", Blob.class);
  }

  @Override
  public Blob defaultValue(ValuesDb valuesDb) {
    return valuesDb.blobBuilder().build();
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
