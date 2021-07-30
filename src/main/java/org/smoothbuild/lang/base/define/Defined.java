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
  private final ModulePath modulePath;
  private final String name;

  public Defined(Type type, ModulePath modulePath, String name, Location location) {
    this.type = requireNonNull(type);
    this.modulePath = requireNonNull(modulePath);
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

  public ModulePath modulePath() {
    return modulePath;
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
