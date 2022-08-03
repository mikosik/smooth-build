package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tapanal;
import org.smoothbuild.lang.type.TypeS;

/**
 * Type definition.
 */
public class TDefS extends Tapanal {
  public TDefS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
