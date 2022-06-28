package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.Side.LOWER;

/**
 * This class is immutable.
 */
public final class NothingTS extends EdgeTS {
  public NothingTS() {
    super(TNamesS.NOTHING, LOWER);
  }
}
