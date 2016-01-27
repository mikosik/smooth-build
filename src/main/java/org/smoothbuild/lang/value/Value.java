package org.smoothbuild.lang.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

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

  public boolean equals(Object object) {
    return object instanceof Value && equals((Value) object);
  }

  private boolean equals(Value value) {
    return Objects.equals(hash, value.hash());
  }

  public int hashCode() {
    return hash.hashCode();
  }
}
