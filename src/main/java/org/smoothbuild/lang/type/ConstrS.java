package org.smoothbuild.lang.type;

import org.smoothbuild.util.type.Side;

/**
 * Type constraint.
 */
public record ConstrS(MonoTS lower, MonoTS upper) {
  public static ConstrS constrS(MonoTS lower, MonoTS upper) {
    return new ConstrS(lower, upper);
  }

  public static ConstrS constrS(MonoTS a, MonoTS b, Side bSide) {
    return switch (bSide) {
      case LOWER -> constrS(b, a);
      case UPPER -> constrS(a, b);
    };
  }

  @Override
  public String toString() {
    return "`" + lower.name() + " < " + upper.name() + "`";
  }
}
