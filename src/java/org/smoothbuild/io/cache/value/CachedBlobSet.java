package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.BlobSet;
import org.smoothbuild.lang.function.value.Value;

import com.google.common.hash.HashCode;

public class CachedBlobSet implements BlobSet, Value {
  private final ValueDb valueDb;
  private final HashCode hash;

  public CachedBlobSet(ValueDb valueDb, HashCode hash) {
    this.valueDb = checkNotNull(valueDb);
    this.hash = checkNotNull(hash);
  }

  @Override
  public Type type() {
    return Type.BLOB_SET;
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Iterator<Blob> iterator() {
    return valueDb.blobSetIterable(hash).iterator();
  }
}
