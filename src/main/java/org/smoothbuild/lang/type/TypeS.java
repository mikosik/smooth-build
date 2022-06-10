package org.smoothbuild.lang.type;

import org.smoothbuild.util.collect.Named;

/**
 * Type kind.
 */
public sealed interface TypeS extends Named
    permits PolyTS, MonoTS {

  public static MonoTS hackyCast(TypeS typeS) {
    // TODO handle polymorphic correctly
    return switch (typeS) {
      case MonoTS monoTS -> monoTS;
      case PolyTS polyTS -> polyTS.type();
    };
  }
}
