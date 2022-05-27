package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Patanal;
import org.smoothbuild.lang.type.TypeS;

/**
 * Type definition.
 */
public class TDefS extends Patanal {
  public TDefS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
