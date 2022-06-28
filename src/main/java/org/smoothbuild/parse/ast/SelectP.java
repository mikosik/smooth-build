package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public final class SelectP extends MonoP implements MonoExprP {
  private final ObjP selectable;
  private final String field;

  public SelectP(ObjP selectable, String field, Loc loc) {
    super(loc);
    this.selectable = selectable;
    this.field = field;
  }

  public ObjP selectable() {
    return selectable;
  }

  public String field() {
    return field;
  }
}
