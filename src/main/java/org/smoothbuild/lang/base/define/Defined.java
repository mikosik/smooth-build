package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * This class and all its subclasses are immutable.
 */
public class Defined extends NalImpl {
  private final TypeS type;
  private final ModulePath modulePath;

  public Defined(TypeS type, ModulePath modulePath, String name, Loc loc) {
    super(name, loc);
    this.type = requireNonNull(type);
    this.modulePath = requireNonNull(modulePath);
  }

  public TypeS type() {
    return type;
  }

  public ModulePath modulePath() {
    return modulePath;
  }

  public String extendedName() {
    return name();
  }

  public String typeAndName() {
    return type.name() + " " + extendedName();
  }
}
