package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public final class SelectN extends MonoExprN {
  private final ObjN selectable;
  private final String field;

  public SelectN(ObjN selectable, String field, Loc loc) {
    super(loc);
    this.selectable = selectable;
    this.field = field;
  }

  public ObjN selectable() {
    return selectable;
  }

  public String field() {
    return field;
  }
}
