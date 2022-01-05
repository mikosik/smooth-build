package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Loc.internal;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;

/**
 * This class is immutable.
 */
public final class BoolValS extends ValS {
  private final boolean valJ;

  public BoolValS(boolean valJ, ModPath modPath, TypeFactoryS factory) {
    super(factory.bool(), modPath, Boolean.toString(valJ), internal());
    this.valJ = valJ;
  }

  @Override
  public String toString() {
    return "BoolValS(`" + type().name() + " " + name() + " = " + valJ + "`)";
  }

  public boolean valJ() {
    return valJ;
  }
}



