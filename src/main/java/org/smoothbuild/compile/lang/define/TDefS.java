package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tapanal;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Type definition.
 */
public class TDefS extends Tapanal {
  public TDefS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
