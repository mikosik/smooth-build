package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.function.base.Type.BLOB_SET;

import java.util.Iterator;

import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.BlobSet;

import com.google.common.hash.HashCode;

public class CachedBlobSet extends AbstractValue implements BlobSet {
  private final ValueDb valueDb;

  public CachedBlobSet(ValueDb valueDb, HashCode hash) {
    super(BLOB_SET, hash);
    this.valueDb = checkNotNull(valueDb);
  }

  @Override
  public Iterator<Blob> iterator() {
    return valueDb.blobSetIterable(hash()).iterator();
  }
}
