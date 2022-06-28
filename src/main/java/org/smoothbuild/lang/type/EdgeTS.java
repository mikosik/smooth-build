package org.smoothbuild.lang.type;

import static org.smoothbuild.lang.type.TypeFS.ANY;
import static org.smoothbuild.lang.type.TypeFS.NOTHING;

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

  public static EdgeTS edgeTS(Side side) {
    return switch (side) {
      case LOWER -> NOTHING;
      case UPPER -> ANY;
    };
  }
}

