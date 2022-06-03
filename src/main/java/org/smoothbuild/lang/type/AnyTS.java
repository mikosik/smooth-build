package org.smoothbuild.lang.type;

import static org.smoothbuild.util.type.Side.UPPER;

/**
 * This class is immutable.
 */
public final class AnyTS extends EdgeTS {
  public AnyTS() {
    super(TNamesS.ANY, UPPER);
  }
}
