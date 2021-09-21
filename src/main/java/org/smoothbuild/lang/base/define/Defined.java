package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.Type;

/**
 * This class and all its subclasses are immutable.
 */
public class Defined extends Nal {
  private final Type type;
  private final ModulePath modulePath;

  public Defined(Type type, ModulePath modulePath, String name, Location location) {
    super(name, location);
    this.type = requireNonNull(type);
    this.modulePath = requireNonNull(modulePath);
  }

  public Type type() {
    return type;
  }

  public ModulePath modulePath() {
    return modulePath;
  }

  public String q() {
    return "`" + name() + "`";
  }

  public String extendedName() {
    return name();
  }
}
