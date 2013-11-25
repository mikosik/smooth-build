package org.smoothbuild.io.cache.value.instance;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.lang.type.SValue;

import com.google.common.hash.HashCode;

public abstract class CachedValue implements SValue {
  private final SType<?> type;
  private final HashCode hash;

  public CachedValue(SType<?> type, HashCode hash) {
    this.type = checkNotNull(type);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public SType<?> type() {
    return type;
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof SValue) {
      SValue that = (SValue) object;
      return this.hash.equals(that.hash());
    }
    return false;
  }
}
