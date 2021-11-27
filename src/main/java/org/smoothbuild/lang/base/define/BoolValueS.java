package org.smoothbuild.lang.base.define;

import static org.smoothbuild.lang.base.define.Location.internal;

import org.smoothbuild.lang.base.type.impl.TypeFactoryS;

/**
 * This class is immutable.
 */
public final class BoolValueS extends ValueS {
  private final boolean valJ;

  public BoolValueS(boolean valJ, ModulePath modulePath, TypeFactoryS factory) {
    super(factory.bool(), modulePath, Boolean.toString(valJ), internal());
    this.valJ = valJ;
  }

  @Override
  public String toString() {
    return "Value(`" + type().name() + " " + name() + " = " + valJ + "`)";
  }

  public boolean valJ() {
    return valJ;
  }
}


