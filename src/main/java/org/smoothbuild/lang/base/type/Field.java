package org.smoothbuild.lang.base.type;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.smoothbuild.lang.base.Location;

public class Field {
  private final ConcreteType type;
  private final String name;
  private final Location location;

  public Field(ConcreteType type, String name, Location location) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.location = requireNonNull(location);
  }

  public ConcreteType type() {
    return type;
  }

  public String name() {
    return name;
  }

  public Location location() {
    return location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Field field) {
      return type.equals(field.type) && name.equals(field.name);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name);
  }

  @Override
  public String toString() {
    return "Field{" +
        "type=" + type +
        ", name='" + name +
        '}';
  }
}
