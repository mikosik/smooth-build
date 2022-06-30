package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.common.ObjC;

public final class DefaultArgP extends ArgP {
  public DefaultArgP(ObjC objC, Loc loc) {
    super(Optional.empty(), objC, loc);
    setTypeS(objC.typeS());
  }

  @Override
  public String nameSanitized() {
    return "<default>";
  }
}
