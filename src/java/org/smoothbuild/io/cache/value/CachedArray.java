package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;

import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;

import com.google.common.hash.HashCode;

public class CachedArray<T extends Value> extends AbstractValue implements SArray<T> {
  private final ValueDb valueDb;
  private final ValueReader<T> valueReader;

  public CachedArray(ValueDb valueDb, HashCode hash, Type type, ValueReader<T> valueReader) {
    super(type, hash);
    this.valueReader = checkNotNull(valueReader);
    this.valueDb = checkNotNull(valueDb);
  }

  @Override
  public Iterator<T> iterator() {
    return valueDb.array(hash(), valueReader).iterator();
  }
}
