package org.smoothbuild.lang.plugin;

import java.util.List;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.BlobSet;

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

  public BlobSet build() {
    return valueDb.blobSet(result);
  }
}
