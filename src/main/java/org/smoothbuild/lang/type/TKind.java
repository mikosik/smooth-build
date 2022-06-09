package org.smoothbuild.lang.type;

import org.smoothbuild.util.collect.Named;

/**
 * Type kind.
 */
public sealed interface TKind extends Named
    permits PolyTS, TypeS {

  public static TypeS hackyCast(TKind tKind) {
    // TODO handle polymorphic correctly
    return switch (tKind) {
      case TypeS monoTS -> monoTS;
      case PolyTS polyTS -> polyTS.type();
    };
  }
}
