package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Named;

/**
 * This class and all its subclasses are immutable.
 */
public class Declared implements Named {
  private final Type type;
  private final Location location;
  private final String name;

  public Declared(Type type, String name, Location location) {
    this.type = requireNonNull(type);
    this.location = requireNonNull(location);
    this.name = requireNonNull(name);
  }

  @Override
  public Location location() {
    return location;
  }

  public Type type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  public String q() {
    return "`" + name() + "`";
  }

  public String extendedName() {
    return name();
  }
}
