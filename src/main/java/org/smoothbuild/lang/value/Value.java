package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

public class Value {
  private final Type type;
  private final HashCode hash;

  public Value(Type type, HashCode hash) {
    this.type = type;
    this.hash = checkNotNull(hash);
  }

  public HashCode hash() {
    return hash;
  }

  public Type type() {
    if (type == null) {
      return (Type) this;
    } else {
      return type;
    }
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Value && equals((Value) object);
  }

  private boolean equals(Value value) {
    return Objects.equals(hash, value.hash());
  }

  @Override
  public int hashCode() {
    return hash.hashCode();
  }
}
