package org.smoothbuild.lang.type;

import org.smoothbuild.util.type.Side;

/**
 * This class is immutable.
 */
public sealed abstract class EdgeTS extends BaseTS
    permits AnyTS, NothingTS {
  private final Side side;

  public EdgeTS(String name, Side side) {
    super(name);
    this.side = side;
  }

  public Side side() {
    return side;
  }
}

