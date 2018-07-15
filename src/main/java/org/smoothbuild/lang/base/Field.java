package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.parse.ast.Named;

public class Field implements Named {
  private final Type type;
  private final String name;
  private final Location location;

  public Field(Type type, String name, Location location) {
    this.type = requireNonNull(type);
    this.name = requireNonNull(name);
    this.location = requireNonNull(location);
  }

  public Type type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Location location() {
    return location;
  }
}
