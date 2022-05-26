package org.smoothbuild.lang.define;

import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.type.TypeS;

/**
 * Referencable.
 */
public class RefableS extends DefinedS implements Refable {
  public RefableS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
