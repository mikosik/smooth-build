package org.smoothbuild.lang.function.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import org.smoothbuild.lang.type.Type;

public class TypedName {
  private final Type type;
  private final Name name;

  public TypedName(Type type, Name name) {
    this.type = checkNotNull(type);
    this.name = checkNotNull(name);
  }

  public Type type() {
    return type;
  }

  public Name name() {
    return name;
  }

  public boolean equals(Object object) {
    if (!(object instanceof TypedName)) {
      return false;
    }
    TypedName that = (TypedName) object;
    return type.equals(that.type)
        && name.equals(that.name);
  }

  public int hashCode() {
    return Objects.hash(type, name);
  }

  public String toString() {
    return type.toString() + " " + name.toString();
  }
}
