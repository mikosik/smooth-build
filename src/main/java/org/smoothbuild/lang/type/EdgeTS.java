package org.smoothbuild.lang.type;

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
      case LOWER -> TypeFS.nothing();
      case UPPER -> TypeFS.any();
    };
  }
}

