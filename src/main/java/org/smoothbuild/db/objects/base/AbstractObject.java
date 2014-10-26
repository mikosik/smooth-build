package org.smoothbuild.db.objects.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;

import com.google.common.hash.HashCode;

public abstract class AbstractObject implements Value {
  private final Type<?> type;
  private final HashCode hash;

  public AbstractObject(Type<?> type, HashCode hash) {
    this.type = checkNotNull(type);
    this.hash = checkNotNull(hash);
  }

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public Type<?> type() {
    return type;
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Value) {
      Value that = (Value) object;
      return this.hash.equals(that.hash());
    }
    return false;
  }
}
