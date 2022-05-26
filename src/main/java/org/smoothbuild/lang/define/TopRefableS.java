package org.smoothbuild.lang.define;

import org.smoothbuild.lang.type.TypeS;

/**
 * Top level evaluable.
 */
public sealed abstract class TopRefableS extends RefableS implements Nal
    permits FuncS, ValS {
  public TopRefableS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
