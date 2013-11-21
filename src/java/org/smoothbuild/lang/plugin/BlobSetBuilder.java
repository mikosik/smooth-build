package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.Blob;

import com.google.common.collect.Lists;

public class BlobSetBuilder {
  private final ValueDb valueDb;
  private final List<Blob> result;

  public BlobSetBuilder(ValueDb valueDb) {
    this.valueDb = valueDb;
    this.result = Lists.newArrayList();
  }

  public void add(Blob blob) {
    result.add(blob);
  }

  public Array<Blob> build() {
    return valueDb.blobSet(result);
  }
}
