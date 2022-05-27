package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.like.Obj;

public final class DefaultArgN extends ArgN {
  public DefaultArgN(Obj obj, Loc loc) {
    super(null, obj, loc);
    setType(obj.typeO());
  }

  @Override
  public String nameSanitized() {
    return "<default>";
  }
}
