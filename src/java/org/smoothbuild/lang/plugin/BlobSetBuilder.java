package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Blob;

public class BlobSetBuilder extends ArrayBuilder<Blob> {
  public BlobSetBuilder(ValueDb valueDb) {
    super(valueDb);
  }

  @Override
  protected Array<Blob> buildImpl(ValueDb valueDb, List<Blob> elements) {
    return valueDb.blobSet(elements);
  }
}
