package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;

public abstract sealed class CnstN extends ObjN
    permits BlobN, IntN, StringN {
  public CnstN(Loc loc) {
    super(loc);
  }
}
