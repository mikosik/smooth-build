package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.parse.ast.Named;

/**
 * This class and all its subclasses are immutable.
 */
public class Defined implements Named {
  private final Type type;
  private final Location location;
  private final String name;

  public Defined(Type type, String name, Location location) {
    this.type = requireNonNull(type);
    this.location = requireNonNull(location);
    this.name = requireNonNull(name);
  }

  public Type type() {
    return type;
  }

  @Override
  public Location location() {
    return location;
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