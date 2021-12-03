package org.smoothbuild.lang.parse.ast;

import org.smoothbuild.lang.base.define.Loc;

public final class SelectN extends ExprN {
  private final ExprN selectable;
  private final String field;

  public SelectN(ExprN selectable, String field, Loc loc) {
    super(loc);
    this.selectable = selectable;
    this.field = field;
  }

  public ExprN selectable() {
    return selectable;
  }

  public String field() {
    return field;
  }
}
