package org.smoothbuild.lang.type;

import org.smoothbuild.util.type.Side;

/**
 * Type constraint.
 */
public record ConstrS(TypeS lower, TypeS upper) {
  public static ConstrS constrS(TypeS lower, TypeS upper) {
    return new ConstrS(lower, upper);
  }

  public static ConstrS constrS(TypeS a, TypeS b, Side bSide) {
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
