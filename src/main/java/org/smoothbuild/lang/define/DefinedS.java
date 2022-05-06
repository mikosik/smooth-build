package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.TypeS;

/**
 * This class and all its subclasses are immutable.
 */
public class DefinedS extends NalImpl {
  private final TypeS type;
  private final ModPath modPath;

  public DefinedS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(name, loc);
    this.type = requireNonNull(type);
    this.modPath = requireNonNull(modPath);
  }

  public TypeS type() {
    return type;
  }

  public ModPath modPath() {
    return modPath;
  }

  public String extendedName() {
    return name();
  }

  public String typeAndName() {
    return type.name() + " " + extendedName();
  }
}
