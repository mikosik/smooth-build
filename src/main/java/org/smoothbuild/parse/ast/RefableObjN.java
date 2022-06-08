package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.RefableObj;

public abstract sealed class RefableObjN extends RefableN implements RefableObj
    permits FuncN, ValN {
  public RefableObjN(String name, Optional<ObjN> body, Optional<AnnN> ann, Loc loc) {
    super(name, body, ann, loc);
  }
}
