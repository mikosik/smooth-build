package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

/**
 * Value in smooth language.
 */
public class Value {
  private final Type type;
  private final HashCode hash;

  public Value(Type type, HashCode hash) {
    this.type = checkNotNull(type);
    this.hash = checkNotNull(hash);
  }

  public HashCode hash() {
    return hash;
  }

  public Type type() {
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
