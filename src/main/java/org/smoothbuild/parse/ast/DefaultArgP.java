package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Obj;

public final class DefaultArgP extends ArgP {
  public DefaultArgP(Obj obj, Loc loc) {
    super(Optional.empty(), obj, loc);
    setTypeO(obj.typeO());
  }

  @Override
  public String nameSanitized() {
    return "<default>";
  }
}
